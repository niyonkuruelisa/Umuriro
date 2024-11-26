package com.niyonkuruelisa.umuriro.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.niyonkuruelisa.umuriro.R;
import com.niyonkuruelisa.umuriro.models.DeviceSettings;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private static final String CHANNEL_ID = "ALARM_CHANNEL";
    private static final int NOTIFICATION_ID = 0;
    private static MediaPlayer mediaPlayer;
    private static Context context;
    private int times  = 0;
    private OfflineStorageService offlineStorageService;
    private DeviceSettings deviceSettings;
    @Override
    public void onReceive(Context context, Intent intent) {
        offlineStorageService = new OfflineStorageService(context);
        deviceSettings = offlineStorageService.getDeviceSettings();
        times = intent.getIntExtra("times", 0);
        Log.d(TAG, "Alarm triggered because the device is not charging: " + times);

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
        if(times == 3 || times == 5){
            Log.d(TAG, "Sending SMS to emergency contacts");
            SendSMSToEmergencyContacts();
        }
        this.context = context;
    }


    private void SendSMSToEmergencyContacts() {
        // Get emergency contacts from local storage
        DeviceSettings deviceSettings = offlineStorageService.getDeviceSettings();
        if(deviceSettings == null){
            Log.d(TAG, "Device settings not found");
            return;
        }

        try{
            // Get the SubscriptionManager and list of active subscriptions
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

            if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
                // Assuming you want to use the second SIM card (index 1)
                // Find carrier subscription info by Network name
                String CarrierName = "";
                if(subscriptionInfoList.get(0).getCarrierName().toString().toLowerCase().contains("airtel")){
                    CarrierName = "Airtel";
                }else if(subscriptionInfoList.get(0).getCarrierName().toString().toLowerCase().contains("mtn")){
                    CarrierName = "MTN";;
                }

                SubscriptionInfo subscriptionInfo = subscriptionInfoList.get(0).getCarrierName().toString().toLowerCase().contains("mtn") ? subscriptionInfoList.get(0) : subscriptionInfoList.get(1);
                int subscriptionId = subscriptionInfo.getSubscriptionId();

                // Get the SmsManager for the specific subscription ID
                SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);

                String message = deviceSettings.getMessageSignature()+" Birihutirwa! Umuriro ushobora kuba wagiye.";

                if (deviceSettings.getPhoneNumber1() != null) {
                    // Send SMS to phone number 1
                    try {
                        smsManager.sendTextMessage(deviceSettings.getPhoneNumber1(), null, message, null, null);
                        Log.d(TAG, "SMS sent to " + deviceSettings.getPhoneNumber1() + " using " + CarrierName + " SIM card");
                    } catch (Exception exception) {
                        Log.d(TAG, "Failed to send SMS to " + deviceSettings.getPhoneNumber1() + ": " + exception.getMessage());
                    }
                }
            } else {
                Log.d(TAG, "No active SIM cards found");
            }
        }catch (Exception exception){
            Log.d(TAG, "Something went wrong: "+exception.getMessage());
        }

        // Send SMS to each emergency contact
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