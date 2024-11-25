package com.niyonkuruelisa.umuriro.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class SMSCheckerService extends Service {
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    private static final long CHECK_INTERVAL = 5000; // 5 seconds
    private Handler handler;
    private Runnable smsCheckRunnable;

    private static final String TAG = "SMSCheckerService";

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        smsCheckRunnable = new Runnable() {
            @Override
            public void run() {

                String newMessage = getLatestSMS();
                if (newMessage != null) {
                    Log.d(TAG, "New SMS: " + newMessage);
                    // add SMS timestamp to local storage so that it does not check it again
                }

                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        // Check if the intent contains the RemoteInput results
        try{
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                CharSequence replyText = remoteInput.getCharSequence("key_text_reply");
                if (replyText != null) {
                    Log.d(TAG, "Reply received: " + replyText);
                    // Handle the reply text here
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.cancel(13);
                }
            }
        }catch (Exception e){
            Log.d("SMSService", "Error: "+e.getMessage());
        }
        handler.post(smsCheckRunnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(smsCheckRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("Range")
    private String getLatestSMS() {

        try{
            Uri uri = Uri.parse("content://sms/inbox");
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, "date DESC");
            String message = null;
            if (cursor != null && cursor.move(new Random().nextInt(cursor.getCount()))) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") long timestamp = cursor.getLong(cursor.getColumnIndex("date"));
                Log.d(TAG, "SMS ID: " + id + ", Timestamp: " + timestamp);
                //TODO: check if we already have sms timestamp in our storage. If we do, return null
                message = cursor.getString(cursor.getColumnIndex("body"));

                cursor.close();
            }

            return message;
        }catch (Exception e){
            Log.d("SMSService", "Error: "+e.getMessage());

            return null;
        }

    }
}