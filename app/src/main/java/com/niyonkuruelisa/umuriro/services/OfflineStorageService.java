package com.niyonkuruelisa.umuriro.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.niyonkuruelisa.umuriro.helpers.Helper;
import com.niyonkuruelisa.umuriro.models.DeviceSettings;

public class OfflineStorageService {
    private static final String PREF_NAME = "OfflineStoragePref";
    private static final String DEVICE_SETTINGS = "settings";
    private static final String TAG = "OfflineStorageService";

    private final Context context;

    public OfflineStorageService(Context context) {
        this.context = context;
    }
    public void ClearAllData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    // create or update device settings

    public void createDeviceSettings(DeviceSettings deviceSettings) {
        String value = Helper.encodeObject(deviceSettings);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEVICE_SETTINGS, value);
        editor.apply();
    }
    // get device settings
    public DeviceSettings getDeviceSettings() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String info = sharedPreferences.getString(DEVICE_SETTINGS, null);
        //convert string to map
        if (info != null) {
            Log.d(TAG, "info is not null");
            Object object = Helper.decodeObject(info);
            if (object instanceof DeviceSettings) {
                Log.d(TAG, "Decoded object is of type DeviceSettings");
                return (DeviceSettings) object;
            } else {
                throw new ClassCastException("Decoded object is not of type DeviceSettings");
            }
        }

        return null;
    }
}
