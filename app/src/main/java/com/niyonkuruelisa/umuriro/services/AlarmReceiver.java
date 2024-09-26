package com.niyonkuruelisa.umuriro.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.niyonkuruelisa.umuriro.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private static final String CHANNEL_ID = "ALARM_CHANNEL";
    private static final int NOTIFICATION_ID = 0;
    private static MediaPlayer mediaPlayer;
    private static Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm triggered because the device is not charging");

        // Play alarm sound
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound); // Ensure you have an alarm_sound.mp3 in res/raw
        }

        // Check if MediaPlayer is not playing and start it
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

        // Create a notification with a stop button
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to stop the alarm
        Intent stopIntent = new Intent(context, StopAlarmReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(com.google.android.material.R.drawable.ic_mtrl_checked_circle)
                .setContentTitle("Umuriro Wagiye")
                .setContentText("Mushake uko musubizeho Umuriro")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(com.google.android.material.R.drawable.ic_mtrl_checked_circle, "Hagarika", stopPendingIntent)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(0, builder.build());
        this.context = context;
    }

    public static MediaPlayer getMediaPlayer() {

        return mediaPlayer;
    }
    public static void setMediaPlayer(MediaPlayer mediaPlayer) {
        AlarmReceiver.mediaPlayer = mediaPlayer;
        if (mediaPlayer == null) {
            cancelNotification();
        }
    }
    private static void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}