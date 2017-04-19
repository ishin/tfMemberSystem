package io.rong.ptt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import io.rong.common.RLog;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.ModuleManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.ptt.message.server.PTTEndMessage;
import io.rong.ptt.message.server.PTTMicHolderChangeMessage;
import io.rong.ptt.message.server.PTTParticipantChangeMessage;
import io.rong.ptt.message.server.PTTPingMessage;
import io.rong.ptt.message.server.PTTStartMessage;
import io.rong.ptt.net.PttHttpManager;
import io.rong.ptt.net.PttResponseCallback;

/**
 * Created by jiangecho on 2016/12/26.
 */

public class PTTClient {
    private static final String TAG = PTTClient.class.getName();
    private PTTClientImpl pttClientImpl;
    private PttHttpManager pttHttpManager;
    private WeakReference<PTTSessionStateListener> pttSessionStateListener;
    private WeakReference<PTTStateListener> pttStateListener;
    private static final long DELTA_MILLIS_TO_DROP_PTT_HISTORY_MESSAGE = 3 * 60 * 1000;

    private String currentUserId;
    private Map<String, PTTSession> pttSessions;
    private Conversation.ConversationType currentConversationType = Conversation.ConversationType.NONE;
    private String currentTargetId;
    private Context context;

    private boolean sendExtraVoiceMessage;
    private long durationPerExtraVoiceMessage;
    private Handler handler;
    private Runnable speakTimeoutRunnable;
    private ModuleManager.MessageRouter pttManagerMessageRouter;

    private static final class HOLDER {
        static PTTClient instance = new PTTClient();
    }

    public static PTTClient getInstance() {
        return HOLDER.instance;
    }

    public void init(Context context) {
        init(context, false, 0);
    }

    public void init(final Context context, boolean sendExtraVoiceMessage, long durationPerExtraVoiceMessage) {
        this.context = context;
        this.sendExtraVoiceMessage = sendExtraVoiceMessage;
        this.handler = new Handler(Looper.getMainLooper());
        this.durationPerExtraVoiceMessage = durationPerExtraVoiceMessage;
        if (pttClientImpl == null) {
            pttHttpManager = new PttHttpManager();
            pttSessions = new Hashtable<>();
        }
        pttManagerMessageRouter = new ModuleManager.MessageRouter() {
            @Override
            public boolean onReceived(final Message msg, int left, boolean offline, int cmdLeft) {
                PTTSession pttSession = pttSessions.get(genPttSessionKey(msg));
                final MessageContent messageContent = msg.getContent();
                if (messageContent instanceof PTTMicHolderChangeMessage) {
                    RLog.i(TAG, "receive " + messageContent.getClass().getSimpleName());
                    if (pttSession == null) {
                        pttSession = new PTTSession(msg.getConversationType(), msg.getTargetId());
                        pttSessions.put(pttSession.key(), pttSession);
                    }
                    pttSession.micHolder = ((PTTMicHolderChangeMessage) messageContent).getHolder();
                    pttMicHolderChange(pttSession);
                } else if (messageContent instanceof PTTParticipantChangeMessage) {
                    RLog.i(TAG, "receive " + messageContent.getClass().getSimpleName()
                            + " "
                            +  (((PTTParticipantChangeMessage) messageContent).getParticipants() != null ? ((PTTParticipantChangeMessage) messageContent).getParticipants().size() : 0));
                    if (pttSession == null) {
                        pttSession = new PTTSession(msg.getConversationType(), msg.getTargetId());
                        pttSessions.put(pttSession.key(), pttSession);
                    }
                    pttSession.setParticipantIds(((PTTParticipantChangeMessage) messageContent).getParticipants());
                    pttParticipantsChange(pttSession);
                } else if (messageContent instanceof PTTStartMessage) {
                    RLog.i(TAG, "receive " + messageContent.getClass().getSimpleName());
                    if (shouldHandlePttMessage(msg)) {
                        pttSession = new PTTSession(msg.getConversationType(), msg.getTargetId());
                        pttSessions.put(pttSession.key(), pttSession);

                        pttSessionStart(pttSession);
                    }
                } else if (messageContent instanceof PTTEndMessage) {
                    RLog.i(TAG, "receive " + messageContent.getClass().getSimpleName());
                    if (shouldHandlePttMessage(msg)) {
                        pttSession = pttSessions.remove(genPttSessionKey(msg));
                        pttSessionEnd(pttSession);
                    }
                } else if (messageContent instanceof PTTPingMessage) {
                    RLog.i(TAG, "receive " + messageContent.getClass().getSimpleName());
                    if (pttSessions.get(genPttSessionKey(msg)) == null) {
                        queryPttStatus(msg.getConversationType(), msg.getTargetId(), new QueryPTTStatusCallback() {
                            @Override
                            public void onSuccess(PTTSession pttSession) {
                                pttSessions.put(pttSession.key(), pttSession);

                                pttSessionStart(pttSession);
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    }
                }
                // attention:
                // 1. PTTExtensionModule still need the messages
                // 2. so must return false.
                //
                return false;
            }
        };
        ModuleManager.addMessageRouter(pttManagerMessageRouter);
        ModuleManager.addConnectivityStateChangedListener(new ModuleManager.ConnectivityStateChangedListener() {
            @Override
            public void onChanged(RongIMClient.ConnectionStatusListener.ConnectionStatus state) {
                if (state != RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                    if (pttStateListener != null && pttStateListener.get() != null) {
                        pttStateListener.get().onNetworkError("network error: " + state.getMessage());
                    }

                    if (pttSessionStateListener != null && pttSessionStateListener.get() != null) {
                        pttSessionStateListener.get().onNetworkError("network error: " + state.getMessage());
                    }
                }
            }
        });
        registerPTTMessages();
    }

    public void unInit() {
        leaveSession();
        ModuleManager.removeMessageRouter(pttManagerMessageRouter);
        pttManagerMessageRouter = null;
        pttHttpManager = null;

        context = null;
        pttSessions = null;
        sendExtraVoiceMessage = false;
        handler = null;
        pttSessionStateListener = null;
        pttStateListener = null;
    }

    private void pttMicHolderChange(final PTTSession pttSession) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pttStateListener != null && pttStateListener.get() != null) {
                    pttStateListener.get().onMicHolderChanged(pttSession, pttSession.getMicHolder());
                }

                if (pttSessionStateListener != null
                        && pttSessionStateListener.get() != null
                        && pttSession.key().equals(genPttSessionKey(currentConversationType, currentTargetId))) {
                    pttSessionStateListener.get().onMicHolderChanged(pttSession, pttSession.getMicHolder());
                }
            }
        });
    }

    private void pttParticipantsChange(final PTTSession pttSession) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pttStateListener != null && pttStateListener.get() != null) {
                    pttStateListener.get().onParticipantChanged(pttSession, pttSession.getParticipantIds());
                }

                if (pttSessionStateListener != null
                        && pttSessionStateListener.get() != null
                        && pttSession.key().equals(genPttSessionKey(currentConversationType, currentTargetId))) {
                    pttSessionStateListener.get().onParticipantChanged(pttSession, pttSession.getParticipantIds());
                }
            }
        });
    }

    private void pttSessionStart(final PTTSession pttSession) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pttStateListener != null && pttStateListener.get() != null) {
                    pttStateListener.get().onSessionStart(pttSession);
                }
            }
        });
    }

    private void pttSessionEnd(final PTTSession pttSession) {
        if (pttSession == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pttStateListener != null && pttStateListener.get() != null) {
                    pttStateListener.get().onSessionTerminated(pttSession);
                }
            }
        });
    }


    // do not need to handle expired ptt message
    private boolean shouldHandlePttMessage(Message message) {
        long deltaTime = System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime() - message.getSentTime();
        return deltaTime < DELTA_MILLIS_TO_DROP_PTT_HISTORY_MESSAGE;
    }


    /**
     * 如果想设置PTT Server url，请在init之前调用此方法
     *
     * @param url
     */
    public static void setPTTServerBaseUrl(String url) {
        Constants.URL_BASE = url;
    }

    // I think joinChannel is better
    public void joinSession(final Conversation.ConversationType conversationType, final String targetId, final JoinSessionCallback callback) {
        joinSession(conversationType, targetId, 0, callback);
    }

    public void joinSession(final Conversation.ConversationType conversationType, final String targetId, int level, final JoinSessionCallback callback) {
        if (callback == null) {
            return;
        }

        pttHttpManager.post(Constants.URL_JOIN_SESSION, getCurrentUserId(), conversationType, targetId, level, new PttResponseCallback() {
            @Override
            public void onSuccess(final JSONObject jsonObject) {

                RLog.i(TAG, "joinSession success");
                int code = jsonObject.optInt("code", -1);
                if (code != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(jsonObject.optString("message"));
                        }
                    });
                    return;
                }

                /**
                 *    "sessionExpireTime": 1800000,
                 *    "channelExpireTime": 180000
                 */

                pttClientImpl = new PTTClientImpl();
                pttClientImpl.init(context, sendExtraVoiceMessage, durationPerExtraVoiceMessage);
                PTTClient.this.currentConversationType = conversationType;
                PTTClient.this.currentTargetId = targetId;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PTTSession pttSession = pttSessions.get(genPttSessionKey(conversationType, targetId));
                        if (pttSession == null) {
                            pttSession = new PTTSession(conversationType, targetId);
                            pttSession.setParticipantIds(Collections.singletonList(currentTargetId));
                            pttSessions.put(genPttSessionKey(conversationType, targetId), new PTTSession(conversationType, targetId));
                        }
                        callback.onSuccess(pttSession.getParticipantIds());
                    }
                });
            }

            @Override
            public void onFail(final String msg) {
                RLog.i(TAG, "joinSession success");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(msg);
                    }
                });
            }
        });
    }

    public void leaveSession() {
        if (pttClientImpl == null) {
            return;
        }
        pttHttpManager.post(Constants.URL_QUIT_SESSION, getCurrentUserId(), currentConversationType, currentTargetId, null);

        pttClientImpl.end();
        pttClientImpl = null;
        currentConversationType = null;
        currentTargetId = null;
    }

    public void requestToSpeak(@NonNull final RequestToSpeakCallback callback) {
        if (pttClientImpl == null) {
            callback.onFail("not init");
            return;
        }
        PTTSession pttSession = pttSessions.get(genPttSessionKey(currentConversationType, currentTargetId));
        List<String> participants = pttSession.getParticipantIds();
        if (participants == null || participants.size() < 2) { // only yourself
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onFail("no participants");
                }
            });
            return;
        }


        pttHttpManager.post(Constants.URL_REQUIRE_CHANNEL, getCurrentUserId(), currentConversationType, currentTargetId, new PttResponseCallback() {
            @Override
            public void onSuccess(final JSONObject jsonObject) {
                int code = jsonObject.optInt("code", -1);
                if (code != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(jsonObject.optString("message"));
                        }
                    });
                    return;
                }

                final long maxMicHoldMillis = jsonObject.optInt("expiredTime", 60) * 1000;
                PTTSession pttSession = pttSessions.get(genPttSessionKey(currentConversationType, currentTargetId));
                List<String> participants = pttSession.getParticipantIds();
                if (participants == null || participants.size() < 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail("no participants");
                        }
                    });
                    return;
                }
                pttClientImpl.action(currentConversationType, currentTargetId, participants.toArray(new String[0]), maxMicHoldMillis);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onReadyToSpeak(maxMicHoldMillis);
                    }
                });

                speakTimeoutRunnable = new Runnable() {
                    @Override
                    public void run() {
                        callback.onSpeakTimeOut();
                    }
                };
                runOnUiThread(speakTimeoutRunnable, maxMicHoldMillis);
            }

            @Override
            public void onFail(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail("can not get mic");
                    }
                });
            }
        });
    }

    public void stopSpeak() {
        if (pttClientImpl == null) {
            return;
        }
        handler.removeCallbacks(speakTimeoutRunnable);
        speakTimeoutRunnable = null; // fix memory leak

        pttHttpManager.post(Constants.URL_RELEASE_CHANNEL, getCurrentUserId(), currentConversationType, currentTargetId, new PttResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                pttClientImpl.over();
            }

            @Override
            public void onFail(String msg) {
                pttClientImpl.over();
            }
        });
    }

    public void setPttStateListener(PTTStateListener listener) {
        if (listener == null) {
            this.pttStateListener = null;
        } else {
            this.pttStateListener = new WeakReference<>(listener);
        }
    }

    public void setPttSessionStateListener(PTTSessionStateListener listener) {
        if (listener == null) {
            this.pttSessionStateListener = null;
        } else {
            this.pttSessionStateListener = new WeakReference<>(listener);
        }
    }

    private void queryPttStatus(final Conversation.ConversationType conversationType, final String targetId, @NonNull final QueryPTTStatusCallback callback) {

        pttHttpManager.post(Constants.URL_PTT_STATUS, getCurrentUserId(), conversationType, targetId, new PttResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                int code = jsonObject.optInt("code", -1);

                if (code != 0) {
                    return;
                }

                JSONObject result = jsonObject.optJSONObject("result");
                if (result == null) {
                    callback.onFail();
                    return;
                }

                JSONArray jsonArray = result.optJSONArray("participants");
                String micHolder = result.optString("channelHolder");
                List<String> participants = new ArrayList<>();
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        participants.add(jsonArray.optString(i));
                    }
                }
                PTTSession pttSession = new PTTSession(conversationType, targetId);
                pttSession.micHolder = micHolder;
                pttSession.setParticipantIds(participants);
                callback.onSuccess(pttSession);
            }

            @Override
            public void onFail(String msg) {
                callback.onFail();
            }
        });
    }

    public PTTSession getActiveSession() {
        return getPttSession(currentConversationType, currentTargetId);
    }

    public PTTSession getPttSession(Conversation.ConversationType conversationType, String targetId) {
        String key = genPttSessionKey(conversationType, targetId);
        if (key == null || pttSessions == null) {
            return null;
        }
        return pttSessions.get(key);
    }

    public PTTSession getCurrentPttSession(){
        String key = genPttSessionKey(currentConversationType, currentTargetId);
        if (key == null || pttSessions == null) {
            return null;
        }
        return pttSessions.get(key);
    }

    public static String genPttSessionKey(Message message) {
        return message.getConversationType().getName() + ":" + message.getTargetId();
    }

    public static String genPttSessionKey(Conversation.ConversationType conversationType, String targetId) {
        if (conversationType == null || targetId == null) {
            return null;
        }
        return conversationType.getName() + ":" + targetId;
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private void runOnUiThread(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    private void registerPTTMessages() {
        try {
            RongIMClient.registerMessageType(PTTStartMessage.class);
            RongIMClient.registerMessageType(PTTEndMessage.class);
            RongIMClient.registerMessageType(PTTParticipantChangeMessage.class);
            RongIMClient.registerMessageType(PTTMicHolderChangeMessage.class);
            RongIMClient.registerMessageType(PTTPingMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }

    }

    private String getCurrentUserId() {
        if (TextUtils.isEmpty(currentUserId)) {
            currentUserId = RongIMClient.getInstance().getCurrentUserId();
        }
        return currentUserId;
    }
}
