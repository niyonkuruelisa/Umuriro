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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.niyonkuruelisa.umuriro.models.DeviceSettings;
import com.niyonkuruelisa.umuriro.services.OfflineStorageService;
import com.niyonkuruelisa.umuriro.services.StopAlarmReceiver;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 2;
    public boolean serviceStarted =  false;
    private SwitchCompat powerCheckSwitchButton;

    private AlertDialog initialSettingsDialog;
    @SuppressLint({"SetTextI18n", "InlinedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        OfflineStorageService offlineStorageService = new OfflineStorageService(this);
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


        // check if device has settings if not then ask for them
        // get saved settings and log them
        DeviceSettings savedSettings = offlineStorageService.getDeviceSettings();
        // Clear everything for now to make sure we are getting the right settings
        offlineStorageService.ClearAllData();

        if(savedSettings == null){
            // display a custom dialog, with OK button to ask for settings
            Log.d("DeviceSettings", "No settings found, asking for them");
            chooseInitSettingsDialog();
        }
        // Save initial settings
    }

    private void chooseInitSettingsDialog(){
        // Display a dialog to ask for settings
        // Save the settings

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_initial_settings, null);
        Button monitorButton = dialogView.findViewById(R.id.monitorButton);
        Button recevierButton = dialogView.findViewById(R.id.receiverButton);
        DeviceSettings deviceSettings = new DeviceSettings();
        monitorButton.setOnClickListener(v -> {
            deviceSettings.setIsMonitor(true);

            monitorButton.setBackgroundColor(getResources().getColor(R.color.black));
            monitorButton.setTextColor(getResources().getColor(R.color.white));

            recevierButton.setBackgroundColor(getResources().getColor(R.color.color_button_primary));
            recevierButton.setTextColor(getResources().getColor(R.color.black));
        });
        recevierButton.setOnClickListener(v -> {
            deviceSettings.setIsMonitor(false);

            recevierButton.setBackgroundColor(getResources().getColor(R.color.black));
            recevierButton.setTextColor(getResources().getColor(R.color.white));


            monitorButton.setBackgroundColor(getResources().getColor(R.color.color_button_primary));
            monitorButton.setTextColor(getResources().getColor(R.color.black));
        });
        Button okButton = dialogView.findViewById(R.id.okButton);

        okButton.setOnClickListener(v -> {
            if(deviceSettings.isInitialized()){

                initialSettingsDialog.dismiss();
            }else{
                Toast.makeText(this, "Ningombwa ko wuzuza igenamiterere.", Toast.LENGTH_LONG).show();
            }
        });
        builder.setView(dialogView);
        builder.setCancelable(false);
        initialSettingsDialog = builder.create();
        initialSettingsDialog.show();
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