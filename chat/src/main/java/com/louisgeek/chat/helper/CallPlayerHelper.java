package com.louisgeek.chat.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.louisgeek.chat.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by louisgeek on 2019/10/23.
 */
@Deprecated
public class CallPlayerHelper {
    private static MediaPlayer mMediaPlayer;
    private static Ringtone mRingtone;
    private static Timer mTimer;


    ///
    public static void playCallInRing(Context context, boolean isPlay) {
        if (!isPlay) {
            return;
        }
//        AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setMode(AudioManager.MODE_RINGTONE);
//        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        int resId = R.raw.shui_di_call_in;
        Uri ringtoneUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
        //使用 RingtoneManager 同时 铃声音量不是 min 才能显示 miui调整音量才能出现铃声图标
        //todo 直接使用 MediaPlayer 不行 但是 RingtoneManager & Ringtone 里面也用 MediaPlayer
        mRingtone = RingtoneManager.getRingtone(context, ringtoneUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mRingtone.setLooping(true);
            mRingtone.play();
        } else {
            //变相实现 重新播放
            mRingtone.play();
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!mRingtone.isPlaying()) {
                        mRingtone.play();
                    }
                }
            }, 1000 * 1, 1000 * 1);
        }
    }

    public static void playCallEndRing(Context context) {
//        AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setMode(AudioManager.MODE_RINGTONE);
//        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Uri ringtoneUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.shui_di_call_end);
        //使用 RingtoneManager 同时 铃声音量不是 min 才能显示 miui调整音量才能出现铃声图标
        //todo 直接使用 MediaPlayer 不行 但是 RingtoneManager & Ringtone 里面也用 MediaPlayer
        mRingtone = RingtoneManager.getRingtone(context, ringtoneUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mRingtone.setLooping(false);
            mRingtone.play();
        } else {
            mRingtone.play();
        }
    }

    public static void playCallRingTest(Context context, int resId) {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        mMediaPlayer = MediaPlayer.create(context, resId);
        if (mMediaPlayer == null) {
            return;
        }
        try {
            mMediaPlayer.setLooping(false);
            //MediaPlayer.create 已经调用过 prepare
//            mMediaPlayer.prepare();
//            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.release();
            }
        });
    }

    @Deprecated
    public static void playCallInRingOld(Context context) {
//        MyAudioManger.get().setRingtoneModeUseSpeaker();
        //
//        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE);
//        if (uri != null) {
//            mMediaPlayer = MediaPlayer.create(mContext, uri);
//        }
//        if (mMediaPlayer == null) {
//            mMediaPlayer = MediaPlayer.create(mContext,
//                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
//        }
        //微信
        int resId = R.raw.shui_di_call_in;
        mMediaPlayer = MediaPlayer.create(context, resId);
        if (mMediaPlayer == null) {
            return;
        }
        try {
//            mMediaPlayer.stop();
            mMediaPlayer.setLooping(true);
            //MediaPlayer.create 已经调用过 prepare
//            mMediaPlayer.prepare();
            //#####      mMediaPlayer.prepareAsync();
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.release();
            }
        });
       /* try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {800, 150, 400, 130}; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Deprecated
    public static void playCallEndRingOld(Context context) {
//        MyAudioManger.get().setRingtoneModeUseSpeaker();
        //
//        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE);
//        if (uri != null) {
//            mMediaPlayer = MediaPlayer.create(mContext, uri);
//        }
//        if (mMediaPlayer == null) {
//            mMediaPlayer = MediaPlayer.create(mContext,
//                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
//        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }

        //微信
        mMediaPlayer = MediaPlayer.create(context, R.raw.shui_di_call_end);
        if (mMediaPlayer == null) {
            return;
        }
        try {
            mMediaPlayer.setLooping(true);
            //MediaPlayer.create 已经调用过 prepare
//            mMediaPlayer.prepare();
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.release();
            }
        });
       /* try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {800, 150, 400, 130}; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mRingtone != null) {
            mRingtone.stop();
            mRingtone = null;
        }
    }

}
