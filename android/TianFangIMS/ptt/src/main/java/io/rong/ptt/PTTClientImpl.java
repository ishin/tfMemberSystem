package io.rong.ptt;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import io.kvh.media.amr.AmrDecoder;
import io.kvh.media.amr.AmrEncoder;
import io.rong.common.RLog;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.ModuleManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.VoiceMessage;
import io.rong.ptt.message.PTTMessage;

/**
 * Created by jiangecho on 2016/12/26.
 */

class PTTClientImpl {
    private static final String TAG = PTTClientImpl.class.getName();

    //8 k * 16bit * 1 = 8k shorts
    private static final int SAMPLE_RATE = 8000;

    //20 ms
    //0.02 x 8000 x 2 = 320;160 short
    private static final int PCM_FRAME_SIZE = 160;
    private static final int AMR_FRAME_SIZE = 32;

    // (1000 ms / 20 ) x 32 -> one ptt message per second
    // use to set up AudioRecord and AudioTrack, and the message size
    private static final int DEFAULT_BUFFER_SIZE = AMR_FRAME_SIZE * 50;

    private static final int MIN_DURATION_PER_VOICE_MESSAGE = 1000;

    private RongIMClient rongIMClient;
    private AudioPlayThread audioPlayThread;
    private SpeakThread speakThread;

    private State state = State.IDLE;
    private ModuleManager.MessageRouter pttMessageRouter;
    private Context context;


    private long durationPerExtraVoiceMessage;
    private boolean sendExtraVoiceMessage;

    PTTClientImpl() {
    }

    void init(Context context) {
        init(context, false, 0);
    }

    /**
     * @param context
     * @param sendExtraVoiceMessage        语音对讲的过程中，是否需要将对讲的语音作为额外的语音消息发送出去
     * @param durationPerExtraVoiceMessage 当需要额外发送语音消息是，语音消息时长，单位为:ms
     */
    void init(Context context, boolean sendExtraVoiceMessage, long durationPerExtraVoiceMessage) {
        this.context = context;
        state = State.LISTEN;
        this.sendExtraVoiceMessage = sendExtraVoiceMessage;
        this.durationPerExtraVoiceMessage = durationPerExtraVoiceMessage > MIN_DURATION_PER_VOICE_MESSAGE ? durationPerExtraVoiceMessage : MIN_DURATION_PER_VOICE_MESSAGE;
        rongIMClient = RongIMClient.getInstance();
        try {
            RongIMClient.registerMessageType(PTTMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }
        pttMessageRouter = new ModuleManager.MessageRouter() {
            @Override
            public boolean onReceived(Message msg, int left, boolean offline, int cmdLeft) {
                MessageContent messageContent = msg.getContent();
                if (messageContent instanceof PTTMessage) {
                    RLog.e(TAG, "receive ptt message");
                    if (state == State.LISTEN) {
                        play((PTTMessage) messageContent);
                    }
                    return true;
                }
                return false;
            }
        };
        ModuleManager.addMessageRouter(pttMessageRouter);
    }

    void end() {
        playOver();
        over();
        ModuleManager.removeMessageRouter(pttMessageRouter);
        state = State.IDLE;
    }

    void action(Conversation.ConversationType conversationType, String targetId, String[] userIds, long maxDurationMillis) {
        if (conversationType == null || TextUtils.isEmpty(targetId) || userIds == null || userIds.length == 0) {
            RLog.e(TAG, "params error");
            return;
        }
        playOver();
        state = State.SPEAK;

        if (speakThread == null) {
            speakThread = new SpeakThread(conversationType, targetId, userIds, maxDurationMillis);
            speakThread.start();
        }
    }

    void over() {
        if (speakThread != null) {
            speakThread.speakOver();
        }
    }

    private void play(PTTMessage pttMessage) {
        if (audioPlayThread == null) {
            audioPlayThread = new AudioPlayThread();
            audioPlayThread.start();
        }
        audioPlayThread.play(pttMessage.getRawData());
    }

    private void playOver() {
        if (audioPlayThread != null) {
            audioPlayThread.stopPlay();
        }
    }

    private class SpeakThread extends Thread {
        private long maxDurationMillis;
        private long startMillis = 0;
        private boolean speakOver;
        private int bufferSize = 4096;
        private AudioRecord audioRecord;
        private Conversation.ConversationType conversationType;
        private String targetId;
        private String[] userIds;

        final private byte[] header = new byte[]{0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A};
        private File voiceFile;
        private DataOutputStream voiceDataOutputStream;

        SpeakThread(Conversation.ConversationType conversationType, String targetId, String[] userIds, long maxDurationMillis) {
            this.conversationType = conversationType;
            this.targetId = targetId;
            this.userIds = userIds;
            this.maxDurationMillis = maxDurationMillis;
        }

        public void speakOver() {
            this.speakOver = true;
            interrupt();
        }

        private void sendPttMessage(byte[] amrData, int len) {
            PTTMessage pttMessage = PTTMessage.obtain(amrData, len);
            rongIMClient.sendDirectionalMessage(conversationType, targetId, pttMessage, userIds, null, null, null);
        }

        private void writeToVoiceFile(byte[] armData, int len) {
            try {
                if (voiceFile == null) {
                    voiceFile = new File(context.getCacheDir(), System.currentTimeMillis() + "_tmp.amr");
                    voiceDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(voiceFile)));
                    voiceDataOutputStream.write(header);
                }
                voiceDataOutputStream.write(armData, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
                RLog.e(TAG, "writeAmrToFileFailed: " + e.getMessage());
            }
        }

        private void sendVoiceMessage(int duration) {
            try {
                voiceDataOutputStream.close();
                final File file = voiceFile;
                voiceFile = null;
                VoiceMessage voiceMessage = VoiceMessage.obtain(Uri.fromFile(file), duration);
                rongIMClient.sendDirectionalMessage(conversationType, targetId, voiceMessage, userIds, null, null, new IRongCallback.ISendMessageCallback() {

                    @Override
                    public void onAttached(Message message) {
                    }

                    @Override
                    public void onSuccess(Message message) {
                        ModuleManager.getListener().onReceived(message, 0);
                        file.delete();
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        file.delete();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                RLog.e(TAG, "send voice Message Failed: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

            bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            bufferSize = bufferSize > DEFAULT_BUFFER_SIZE ? bufferSize : DEFAULT_BUFFER_SIZE;
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                speakThread = null;
                // TODO notify pttManager?
                RLog.e(TAG, "Audio record can't initialize!");
                return;
            }
            short[] pcmBuffer = new short[PCM_FRAME_SIZE];
            byte[] amrBuffer = new byte[AMR_FRAME_SIZE];
            audioRecord.startRecording();

            int readLen;
            startMillis = System.currentTimeMillis();

            ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            int encodedLen = 0;
            long currentMillis = System.currentTimeMillis();

            AmrEncoder.init(0);
            while (!speakOver && (currentMillis = System.currentTimeMillis()) - startMillis <= maxDurationMillis) {
                readLen = audioRecord.read(pcmBuffer, 0, PCM_FRAME_SIZE);
                if (readLen != PCM_FRAME_SIZE) {
                    RLog.e(TAG, "error: " + readLen);
                    continue;
                }
                encodedLen += AmrEncoder.encode(AmrEncoder.Mode.MR122.ordinal(), pcmBuffer, amrBuffer);
                byteBuffer.put(amrBuffer);
                if (encodedLen >= DEFAULT_BUFFER_SIZE) {
                    sendPttMessage(byteBuffer.array(), encodedLen);
                    writeToVoiceFile(byteBuffer.array(), encodedLen);
                    encodedLen = 0;
                    byteBuffer.clear();

                    if (sendExtraVoiceMessage && currentMillis - startMillis > durationPerExtraVoiceMessage) {
                        sendVoiceMessage((int) (durationPerExtraVoiceMessage / 1000));
                    }
                }

            }

            if (encodedLen > 0) {
                sendPttMessage(byteBuffer.array(), encodedLen);
                writeToVoiceFile(byteBuffer.array(), encodedLen);
                if (sendExtraVoiceMessage) {
                    int seconds = (int) (((currentMillis - startMillis) % durationPerExtraVoiceMessage) / 1000);
                    sendVoiceMessage(seconds > 0 ? seconds : 1);
                }
            }

            AmrEncoder.exit();

            audioRecord.stop();
            audioRecord.release();
            state = PTTClientImpl.State.LISTEN;
            speakThread = null;
        }
    }

    private class AudioPlayThread extends Thread {

        private BlockingQueue<byte[]> contentQueue;
        private int bufferSize;

        AudioPlayThread() {
            contentQueue = new LinkedBlockingDeque<>();
        }

        void play(byte[] rawData) {
            contentQueue.offer(rawData);
        }

        void stopPlay() {
            interrupt();
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            bufferSize = bufferSize > DEFAULT_BUFFER_SIZE ? bufferSize : DEFAULT_BUFFER_SIZE;

            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
            audioTrack.play();
            byte[] rawData;
            byte[] amrData = new byte[AMR_FRAME_SIZE];
            short[] pcmBuffer = new short[PCM_FRAME_SIZE];
            long state = AmrDecoder.init();

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    rawData = contentQueue.take();

                    for (int i = 0; i < rawData.length / AMR_FRAME_SIZE; i++) {
                        System.arraycopy(rawData, i * AMR_FRAME_SIZE, amrData, 0, AMR_FRAME_SIZE);
                        AmrDecoder.decode(state, amrData, pcmBuffer);
                        audioTrack.write(pcmBuffer, 0, pcmBuffer.length);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AmrDecoder.exit(state);
            audioTrack.stop();
            audioTrack.release();

            audioPlayThread = null;
        }
    }

    enum State {
        IDLE,
        SPEAK,
        LISTEN
    }
}
