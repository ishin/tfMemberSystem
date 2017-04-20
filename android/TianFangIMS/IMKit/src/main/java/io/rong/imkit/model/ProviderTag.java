package io.rong.imkit.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.rong.imlib.model.MessageContent;

/**
 * Created by DragonJ on 14-9-15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProviderTag {

    /**
     * 是否显示头像。
     *
     * @return 是否显示头像。
     */
    boolean showPortrait() default true;

    /**
     * 是否横向居中显示。
     *
     * @return 是否横向居中显示。
     */
    boolean centerInHorizontal() default false;

    /**
     * 是否隐藏消息。
     *
     * @return 是否隐藏消息。
     */
    boolean hide() default false;

    /**
     * 是否显示未发生成功警告。
     *
     * @return 是否显示未发生成功警告。
     */
    boolean showWarning() default true;

    /**
     * 是否显示发送进度。
     *
     * @return 是否现实发送进度。
     */
    boolean showProgress() default true;

    /**
     * 会话界面是否在消息上面显示昵称。
     *
     * @return 是否显示
     */
    boolean showSummaryWithName() default true;

    /**
     * Private 会话中是否在消息旁边显示已读回执状态。
     * 默认不显示
     *
     * @return 是否显示
     */
    boolean showReadState() default false;

    Class<? extends MessageContent> messageContent();
}
