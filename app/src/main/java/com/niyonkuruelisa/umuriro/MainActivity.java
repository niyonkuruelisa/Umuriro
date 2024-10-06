package com.niyonkuruelisa.umuriro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.niyonkuruelisa.umuriro.services.StopAlarmReceiver;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 2;
    public boolean serviceStarted =  false;
    private SwitchCompat powerCheckSwitchButton;
    @SuppressLint({"SetTextI18n", "InlinedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        powerCheckSwitchButton = findViewById(R.id.buttonSwitch);
        powerCheckSwitchButton.setChecked(false);
        powerCheckSwitchButton.setEnabled(false);
        powerCheckSwitchButton.setText("Tangira gucunga");
        requestIgnoreBatteryOptimizations();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        } else {
            powerCheckSwitchButton.setEnabled(true);
        }

        powerCheckSwitchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                powerCheckSwitchButton.setText("Hagarika gucunga...");
                serviceStarted =  true;
                startChargingService();
            }else{
                powerCheckSwitchButton.setText("Tangira gucunga");
                //we need to stop the service
                serviceStarted = false;
                Intent stopIntent = new Intent(buttonView.getContext(), StopAlarmReceiver.class);
                buttonView.getContext().sendBroadcast(stopIntent);
                Intent intent = new Intent(MainActivity.this, com.niyonkuruelisa.umuriro.services.ChargingService.class);
                stopService(intent);
            }
        });

    }
    private void requestIgnoreBatteryOptimizations() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                powerCheckSwitchButton.setEnabled(true);
            } else {
                // Permission denied, handle accordingly
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            }
        }
    }
    private void startChargingService() {
        Intent intent = new Intent(this, com.niyonkuruelisa.umuriro.services.ChargingService.class);
        ContextCompat.startForegroundService(this, intent);
        Log.d("ChargingService", "Service created outside.");
    }
}