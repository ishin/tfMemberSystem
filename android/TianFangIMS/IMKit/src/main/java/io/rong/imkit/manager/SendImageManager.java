package io.rong.imkit.manager;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;


public class SendImageManager {
    private final static String TAG = "SendImageManager";

    private ExecutorService executorService;
    private UploadController uploadController;

    static class SingletonHolder {
        static SendImageManager sInstance = new SendImageManager();
    }

    public static SendImageManager getInstance() {
        return SingletonHolder.sInstance;
    }

    private SendImageManager() {
        executorService = getExecutorService();
        uploadController = new UploadController();
    }

    public void sendImages(Conversation.ConversationType conversationType, String targetId, List<Uri> imageList, boolean isFull) {
        RLog.d(TAG, "sendImages " + imageList.size());
        for (Uri image : imageList) {
            ImageMessage content = ImageMessage.obtain(image, image, isFull);
            RongIM.OnSendMessageListener listener = RongContext.getInstance().getOnSendMessageListener();
            if (listener != null) {
                Message message = listener.onSend(Message.obtain(targetId, conversationType, content));
                if (message != null) {
                    RongIMClient.getInstance().insertMessage(conversationType,
                            targetId,
                            null,
                            message.getContent(),
                    new RongIMClient.ResultCallback<Message>() {
                        @Override
                        public void onSuccess(Message message) {
                            message.setSentStatus(Message.SentStatus.SENDING);
                            RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENDING, null);
                            RongContext.getInstance().getEventBus().post(message);
                            uploadController.execute(message);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {

                        }
                    });
                }
            } else {
                RongIMClient.getInstance().insertMessage(conversationType,
                        targetId,
                        null,
                        content,
                new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                        message.setSentStatus(Message.SentStatus.SENDING);
                        RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENDING, null);
                        RongContext.getInstance().getEventBus().post(message);
                        uploadController.execute(message);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
            }
        }
    }

    public void cancelSendingImages(Conversation.ConversationType conversationType, String targetId) {
        RLog.d(TAG, "cancelSendingImages");
        if (conversationType != null && targetId != null && uploadController != null)
            uploadController.cancel(conversationType, targetId);
    }

    public void cancelSendingImage(Conversation.ConversationType conversationType, String targetId, int messageId) {
        RLog.d(TAG, "cancelSendingImages");
        if (conversationType != null && targetId != null && uploadController != null && messageId > 0)
            uploadController.cancel(conversationType, targetId, messageId);
    }

    public void reset() {
        uploadController.reset();
    }

    private class UploadController implements Runnable {
        final List<Message> pendingMessages;
        Message executingMessage;

        public UploadController() {
            this.pendingMessages = new ArrayList<>();
        }

        public void execute(Message message) {
            synchronized (pendingMessages) {
                pendingMessages.add(message);
                if (executingMessage == null) {
                    executingMessage = pendingMessages.remove(0);
                    executorService.submit(this);
                }
            }
        }

        public void reset() {
            RLog.w(TAG, "Rest Sending Images.");
            synchronized (pendingMessages) {
                for (Message message : pendingMessages) {
                    message.setSentStatus(Message.SentStatus.FAILED);
                    RongContext.getInstance().getEventBus().post(message);
                }
                pendingMessages.clear();
            }
            if (executingMessage != null) {
                executingMessage.setSentStatus(Message.SentStatus.FAILED);
                RongContext.getInstance().getEventBus().post(executingMessage);
                executingMessage = null;
            }
        }

        public void cancel(Conversation.ConversationType conversationType, String targetId) {
            synchronized (pendingMessages) {
                int count = pendingMessages.size();
                for (int i = 0; i < count; i++) {
                    Message msg = pendingMessages.get(i);
                    if (msg.getConversationType().equals(conversationType) && msg.getTargetId().equals(targetId)) {
                        pendingMessages.remove(msg);
                    }
                }
                if (pendingMessages.size() == 0)
                    executingMessage = null;
            }
        }

        public void cancel(Conversation.ConversationType conversationType, String targetId, int messageId) {
            synchronized (pendingMessages) {
                int count = pendingMessages.size();
                for (int i = 0; i < count; i++) {
                    Message msg = pendingMessages.get(i);
                    if (msg.getConversationType().equals(conversationType)
                            && msg.getTargetId().equals(targetId)
                            && msg.getMessageId() == messageId) {
                        pendingMessages.remove(msg);
                        break;
                    }
                }
                if (pendingMessages.size() == 0)
                    executingMessage = null;
            }
        }

        private void polling() {
            synchronized (pendingMessages) {
                RLog.d(TAG, "polling " + pendingMessages.size());
                if (pendingMessages.size() > 0) {
                    executingMessage = pendingMessages.remove(0);
                    executorService.submit(this);
                } else {
                    pendingMessages.clear();
                    executingMessage = null;
                }
            }
        }

        @Override
        public void run() {
            RongIM.getInstance().sendImageMessage(executingMessage, null, null, new RongIMClient.SendImageMessageCallback() {
                @Override
                public void onAttached(Message message) {

                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode code) {
                    polling();
                }

                @Override
                public void onSuccess(Message message) {
                    polling();
                }

                @Override
                public void onProgress(Message message, int progress) {

                }
            });
        }
    }

    private ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(1,
                    Integer.MAX_VALUE,
                    60,
                    TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    threadFactory("Rong SendMediaManager", false));
        }
        return executorService;
    }

    private ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(@Nullable Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }
}
