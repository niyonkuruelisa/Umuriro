package com.niyonkuruelisa.umuriro.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, com.niyonkuruelisa.umuriro.services.ChargingService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
            Log.d("BootReceiver", "ChargingService started after boot.");
        }
    }
}