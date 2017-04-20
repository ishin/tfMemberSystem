package io.rong.imkit.manager;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;

import java.io.IOException;

import io.rong.common.RLog;

public class AudioPlayManager implements SensorEventListener {
    private final static String TAG = "AudioPlayManager";

    private MediaPlayer _mediaPlayer;
    private IAudioPlayListener _playListener;
    private Uri _playingUri;
    private Sensor _sensor;
    private SensorManager _sensorManager;
    private AudioManager _audioManager;
    private PowerManager _powerManager;
    private PowerManager.WakeLock _wakeLock;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    private Context context;

    static class SingletonHolder {
        static AudioPlayManager sInstance = new AudioPlayManager();
    }

    public static AudioPlayManager getInstance() {
        return SingletonHolder.sInstance;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];

        if (_sensor == null || _mediaPlayer == null) {
            return;
        }

        if (_mediaPlayer.isPlaying()) {
            if (range > 0.0) {
                //处理 sensor 出现异常后，持续回调 sensor 变化，导致声音播放卡顿
                if(_audioManager.getMode() == AudioManager.MODE_NORMAL) return;
                _audioManager.setMode(AudioManager.MODE_NORMAL);
                _audioManager.setSpeakerphoneOn(true);
                final int positions = _mediaPlayer.getCurrentPosition();
                try {
                    _mediaPlayer.reset();
                    _mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    _mediaPlayer.setVolume(1, 1);
                    _mediaPlayer.setDataSource(context, _playingUri);
                    _mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.seekTo(positions);
                        }
                    });
                    _mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    _mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setScreenOn();
            } else {
                setScreenOff();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    if(_audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) return;
                    _audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                } else {
                    if(_audioManager.getMode() == AudioManager.MODE_IN_CALL) return;
                    _audioManager.setMode(AudioManager.MODE_IN_CALL);
                }
                _audioManager.setSpeakerphoneOn(false);
                //Auto set volume to max. Or no sound.
//                _mediaPlayer.setVolume(1.0f, 1.0f);
//                int maxVolume = _audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//                _audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FX_FOCUS_NAVIGATION_RIGHT);
                replay();
            }
        } else {
            if (range > 0.0) {
                if(_audioManager.getMode() == AudioManager.MODE_NORMAL) return;
                _audioManager.setMode(AudioManager.MODE_NORMAL);
                _audioManager.setSpeakerphoneOn(true);
                setScreenOn();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setScreenOff() {
        if (_wakeLock == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                _wakeLock = _powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
            } else {
                //TODO
                RLog.e(TAG, "Does not support on level " + Build.VERSION.SDK_INT);
            }
        }
        if (_wakeLock != null) {
            _wakeLock.acquire();
        }
    }

    private void setScreenOn() {
        if (_wakeLock != null) {
            _wakeLock.setReferenceCounted(false);
            _wakeLock.release();
            _wakeLock = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void replay() {
        try {
            _mediaPlayer.reset();
            _mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            _mediaPlayer.setVolume(1, 1);
            _mediaPlayer.setDataSource(context, _playingUri);
            _mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            _mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPlay(final Context context, Uri audioUri, IAudioPlayListener playListener) {
        if (context == null || audioUri == null) {
            RLog.e(TAG, "startPlay context or audioUri is null.");
            return;
        }
        this.context = context;

        if (_playListener != null && _playingUri != null) {
            _playListener.onStop(_playingUri);
        }
        resetMediaPlayer();

        this.afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                RLog.d(TAG, "OnAudioFocusChangeListener " + focusChange);
                if (_audioManager != null && focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    _audioManager.abandonAudioFocus(afChangeListener);
                    afChangeListener = null;
                    resetMediaPlayer();
                }
            }
        };

        try {
            _powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            _audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (!_audioManager.isWiredHeadsetOn()) {
                _sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                _sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                _sensorManager.registerListener(this, _sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            muteAudioFocus(_audioManager, true);

            _playListener = playListener;
            _playingUri = audioUri;
            _mediaPlayer = new MediaPlayer();
            _mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (_playListener != null) {
                        _playListener.onComplete(_playingUri);
                        _playListener = null;
                        AudioPlayManager.this.context = null;
                    }
                    reset();
                }
            });
            _mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    reset();
                    return true;
                }
            });
            _mediaPlayer.setDataSource(context, audioUri);
            _mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //_mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            _mediaPlayer.prepare();
            _mediaPlayer.start();
            if (_playListener != null)
                _playListener.onStart(_playingUri);
        } catch (Exception e) {
            e.printStackTrace();
            if (_playListener != null) {
                _playListener.onStop(audioUri);
                _playListener = null;
            }
            reset();
        }
    }

    public void setPlayListener(IAudioPlayListener listener) {
        this._playListener = listener;
    }

    public void stopPlay() {
        if (_playListener != null && _playingUri != null) {
            _playListener.onStop(_playingUri);
        }
        reset();
    }

    private void reset() {
        resetMediaPlayer();
        resetAudioPlayManager();
    }

    private void resetAudioPlayManager() {
        if (_audioManager != null) {
            muteAudioFocus(_audioManager, false);
        }
        if (_sensorManager != null) {
            _sensorManager.unregisterListener(this);
        }
        _sensorManager = null;
        _sensor = null;
        _powerManager = null;
        _audioManager = null;
        _wakeLock = null;

        _playListener = null;
        _playingUri = null;
    }

    private void resetMediaPlayer() {
        if (_mediaPlayer != null) {
            try {
                _mediaPlayer.stop();
                _mediaPlayer.reset();
                _mediaPlayer.release();
                _mediaPlayer = null;
            } catch (IllegalStateException e) {

            }
        }
    }

    public Uri getPlayingUri() {
        return _playingUri;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            // 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus
            RLog.d(TAG, "muteAudioFocus Android 2.1 and below can not stop music");
            return;
        }
        if (bMute) {
            audioManager.requestAudioFocus(afChangeListener, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        } else {
            audioManager.abandonAudioFocus(afChangeListener);
            afChangeListener = null;
        }
    }
}
