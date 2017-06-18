package io.rong.imkit.manager;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

public class InternalModuleManager {
    private final static String TAG = "InternalModuleManager";

    private static IExternalModule callModule;

    private InternalModuleManager() {

    }

    static class SingletonHolder {
        static InternalModuleManager sInstance = new InternalModuleManager();
    }

    public static InternalModuleManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public static void init(Context context) {
        RLog.i(TAG, "init");
        try {
            String moduleName = "io.rong.callkit.RongCallModule";
            Class<?> cls = Class.forName(moduleName);
            Constructor<?> constructor = cls.getConstructor();
            callModule = (IExternalModule) constructor.newInstance();
            callModule.onCreate(context);
        } catch (Exception e) {
            RLog.i(TAG, "Can not find RongCallModule.");
        }
    }

    public void onInitialized(String appKey) {
        RLog.i(TAG, "onInitialized");
        if (callModule != null) {
            callModule.onInitialized(appKey);
        }
    }

    public List<IPluginModule> getExternalPlugins(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModules = new ArrayList<>();
        if (callModule != null
                && (conversationType.equals(Conversation.ConversationType.PRIVATE)
                    || conversationType.equals(Conversation.ConversationType.DISCUSSION)
                    || conversationType.equals(Conversation.ConversationType.GROUP))) {
            pluginModules.addAll(callModule.getPlugins(conversationType));
        }
        return pluginModules;
    }

    public void onConnected(String token) {
        RLog.i(TAG, "onConnected");
        if (callModule != null) {
            callModule.onConnected(token);
        }
    }

    public void onLoaded() {
        RLog.i(TAG, "onLoaded");
        if (callModule != null) {
            callModule.onViewCreated();
        }
    }
}
