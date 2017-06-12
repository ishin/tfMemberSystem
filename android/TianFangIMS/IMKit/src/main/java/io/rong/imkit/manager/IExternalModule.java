package io.rong.imkit.manager;

import android.content.Context;

import java.util.List;

import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by weiqinxiao on 16/8/15.
 */
public interface IExternalModule {
    /**
     * module 构造方法。
     */
    void onCreate(Context context);

    /**
     * SDK 初始化后，调用。
     */
    void onInitialized(String appKey);

    /**
     * SDK {@link io.rong.imlib.RongIMClient#connect(String, RongIMClient.ConnectCallback)} 成功。
     */
    void onConnected(String token);

    /**
     * SDK 中 UI 已加载，相应的 module 可以在此回调中加载自身的 UI。
     */
    void onViewCreated();

    /**
     * module 中注册的 plugin。
     *
     * @param conversationType 不同的会话类型展示不同的 plugin。
     * @return  注册后的 plugin 列表。
     */
    List<IPluginModule> getPlugins(Conversation.ConversationType conversationType);

    /**
     * SDK 断开连接。
     */
    void onDisconnected();
}
