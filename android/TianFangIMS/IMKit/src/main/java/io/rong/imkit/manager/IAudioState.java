package io.rong.imkit.manager;


public abstract class IAudioState {
    void enter() {

    }

    abstract void handleMessage(AudioStateMessage message);
}
