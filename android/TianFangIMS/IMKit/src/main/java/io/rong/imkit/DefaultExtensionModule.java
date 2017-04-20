package io.rong.imkit;

import android.content.Context;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.widget.EditText;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.rong.common.RLog;
import io.rong.imkit.emoticon.EmojiTab;
import io.rong.imkit.emoticon.IEmojiItemClickListener;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

public class DefaultExtensionModule implements IExtensionModule {
    private final static String TAG = DefaultExtensionModule.class.getSimpleName();
    private EditText mEditText;
    private Stack<EditText> stack;
    String[] types = null;


    public DefaultExtensionModule() {
    }

    @Override
    public void onInit(String appKey) {
        stack = new Stack<>();
    }

    @Override
    public void onConnect(String token) {

    }

    @Override
    public void onAttachedToExtension(RongExtension extension) {
        mEditText = extension.getInputEditText();
        Context context = extension.getContext();
        RLog.i(TAG, "attach " + stack.size());
        stack.push(mEditText);
        Resources resources = context.getResources();

        try {
            types = resources.getStringArray(resources.getIdentifier("rc_realtime_support_conversation_types", "array", context.getPackageName()));
        }catch(Resources.NotFoundException e){

        }
    }

    @Override
    public void onDetachedFromExtension() {
        RLog.i(TAG, "detach " + stack.size());
        if(stack.size() > 0) {
            stack.pop();
            mEditText = stack.size() > 0 ? stack.peek() : null;
        }
    }

    @Override
    public void onReceivedMessage(Message message) {

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
//                IPluginModule combineLocation = new CombineLocationPlugin();
//                IPluginModule locationPlugin = new DefaultLocationPlugin();

                boolean typesDefined = false;
                if(types != null && types.length > 0){
                    for(String type: types){
                        if(conversationType.getName().equals(type)){
                            typesDefined = true;
                            break;
                        }
                    }
                }

//                if(typesDefined){
//                    pluginModuleList.add(combineLocation);
//                }else {
//                    if (types == null && conversationType.equals(Conversation.ConversationType.PRIVATE)) {//配置文件中没有类型定义且会话类型为私聊
//                        pluginModuleList.add(combineLocation);
//                    } else {
//                        pluginModuleList.add(locationPlugin);
//                    }
//                }

            }
        } catch (Exception e) {
            RLog.i(TAG, "Not include AMap");
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
            RLog.i(TAG, "Not include Recognizer");
            e.printStackTrace();
        }

        return pluginModuleList;
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        EmojiTab emojiTab = new EmojiTab();
        emojiTab.setOnItemClickListener(new IEmojiItemClickListener() {
            @Override
            public void onEmojiClick(String emoji) {
                int start = mEditText.getSelectionStart();
                mEditText.getText().insert(start, emoji);
            }

            @Override
            public void onDeleteClick() {
                mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        });
        List<IEmoticonTab> list = new ArrayList<>();
        list.add(emojiTab);
        return list;
    }

    @Override
    public void onDisconnect() {

    }
}
