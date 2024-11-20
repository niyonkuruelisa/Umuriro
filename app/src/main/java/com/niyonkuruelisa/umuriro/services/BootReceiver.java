package com.niyonkuruelisa.umuriro.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        OfflineStorageService offlineStorageService = new OfflineStorageService(context);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if(offlineStorageService.getDeviceSettings() != null){
                Intent serviceIntent;
                if(offlineStorageService.getDeviceSettings().isMonitor()){
                    serviceIntent = new Intent(context, com.niyonkuruelisa.umuriro.services.ChargingService.class);
                    Log.d("BootReceiver", "ChargingService started after boot.");
                }else{
                    serviceIntent = new Intent(context, com.niyonkuruelisa.umuriro.services.SMSCheckerService.class);
                    Log.d("BootReceiver", "SMSCheckerService started after boot.");
                }
                ContextCompat.startForegroundService(context, serviceIntent);
            }
        }
    }
}