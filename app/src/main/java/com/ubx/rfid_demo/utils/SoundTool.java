package com.ubx.rfid_demo.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ubx.rfid_demo.R;

/**音效工具类
 * Created by KuCoffee on 2018/2/2.
 */
public class SoundTool {
    protected static volatile SoundTool instance;

    protected final int SOUND_SWIPE_CARD;
    protected final int SOUND_SWIPE_FACE;

    protected SoundPool soundPool;

    private SoundTool(Context context) {
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 1);
        SOUND_SWIPE_CARD = soundPool.load(context, R.raw.scan_buzzer, 1);
        SOUND_SWIPE_FACE = soundPool.load(context,R.raw.scan_new,1);
    }

    public static synchronized SoundTool getInstance(Context context) {
        if(instance == null) {
            instance = new SoundTool(context);
        }
        return instance;
    }

    public void release(){
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            instance = null;
        }
    }

    public void playBeep(int mode) {
        switch (mode){
            case 0:
                return;
            case 1:
                soundPool.play(SOUND_SWIPE_CARD, 1, 1, 1, 0, 1);
                break;
            case 2:
                soundPool.play(SOUND_SWIPE_FACE, 1, 1, 1, 0, 1);
        }
    }

//    /**
//     * 播放刷脸声音
//     */
//    public void playBeepShort() {
//        soundPool.play(SOUND_SWIPE_FACE, 1, 1, 1, 0, 1);
//    }

}
