package io.rong.imkit.manager;

/**
 * Created by weiqinxiao on 16/7/11.
 */
public class AudioStateMessage {
    public int what;
    public Object obj;

    public static AudioStateMessage obtain() {
        return new AudioStateMessage();
    }
}
