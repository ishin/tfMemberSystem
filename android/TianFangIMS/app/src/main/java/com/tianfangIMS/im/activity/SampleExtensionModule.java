package com.tianfangIMS.im.activity;

import android.widget.EditText;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.model.Conversation;

/**
 * Created by LianMengYu on 2017/5/7.
 */

public class SampleExtensionModule extends DefaultExtensionModule {
    String[] types = null;
    private EditText mEditText;
    public SampleExtensionModule() {
    }

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModuleList = new ArrayList<>();
        IPluginModule image = new ImagePlugin();
        IPluginModule file = new FilePlugin();
        pluginModuleList.add(image);
        try {
            String clsName = "com.amap.api.netlocation.AMapNetworkLocationClient";
            Class<?> locationCls = Class.forName(clsName);
            if (locationCls != null) {
                IPluginModule combineLocation = new MyDefaultlocationPlugin();
                IPluginModule locationPlugin = new MyDefaultlocationPlugin();
                boolean typesDefined = false;
                if(types != null && types.length > 0){
                    for(String type: types){
                        if(conversationType.getName().equals(type)){
                            typesDefined = true;
                            break;
                        }
                    }
                }

                if(typesDefined){
                    pluginModuleList.add(combineLocation);
                }else {
                    if (types == null && conversationType.equals(Conversation.ConversationType.PRIVATE)) {//配置文件中没有类型定义且会话类型为私聊
                        pluginModuleList.add(combineLocation);
                    } else {
                        pluginModuleList.add(locationPlugin);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (conversationType.equals(Conversation.ConversationType.GROUP) ||
                conversationType.equals(Conversation.ConversationType.DISCUSSION) ||
                conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            pluginModuleList.addAll(InternalModuleManager.getInstance().getExternalPlugins(conversationType));
        }
        pluginModuleList.add(file);

        try {
            String clsName = "com.iflytek.cloud.SpeechUtility";
            Class<?> cls = Class.forName(clsName);
            if (cls != null) {
                cls = Class.forName("io.rong.recognizer.RecognizePlugin");
                Constructor<?> constructor = cls.getConstructor();
                IPluginModule recognizer = (IPluginModule) constructor.newInstance();
                pluginModuleList.add(recognizer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pluginModuleList;
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return super.getEmoticonTabs();
    }
}
