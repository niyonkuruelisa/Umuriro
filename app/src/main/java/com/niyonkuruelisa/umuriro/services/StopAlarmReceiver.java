package com.niyonkuruelisa.umuriro.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class StopAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer = AlarmReceiver.getMediaPlayer();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            AlarmReceiver.setMediaPlayer(null); // Notify AlarmReceiver that mediaPlayer has been released
        }
    }
}