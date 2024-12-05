package com.niyonkuruelisa.umuriro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.niyonkuruelisa.umuriro.models.DeviceSettings;
import com.niyonkuruelisa.umuriro.services.OfflineStorageService;
import com.niyonkuruelisa.umuriro.services.StopAlarmReceiver;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 2;
    private static final int REQUEST_READ_SMS_PERMISSION = 1;
    private static final int REQUEST_SEND_SMS_PERMISSION = 123;
    private static final int REQUEST_READ_PHONE_STATE = 124;
    public boolean serviceStarted =  false;
    private SwitchCompat powerCheckSwitchButton;

    private AlertDialog initialSettingsDialog = null;
    private boolean isChoosing = false;
    OfflineStorageService offlineStorageService;

    private boolean readSMSPermissionGranted = false;
    private boolean sendSMSPermissionGranted = false;
    private boolean readPhoneStatePermissionGranted = false;
    private boolean requestedSendSMSPermission = false;
    private boolean requestedReadSMSPermission = false;
    private boolean requestedReadPhoneStatePermission = false;

    DeviceSettings deviceSettings = new DeviceSettings();
    @SuppressLint({"SetTextI18n", "InlinedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        offlineStorageService = new OfflineStorageService(this);

        // Checking if this is not the first time the app is being opened
        if(offlineStorageService.getDeviceSettings() != null && offlineStorageService.getDeviceSettings().isInitialized()){
            // checking if the device is a monitor or a receiver
            if(offlineStorageService.getDeviceSettings().isMonitor()){
                setContentView(R.layout.activity_main_monitor);

                powerCheckSwitchButton = findViewById(R.id.buttonSwitch);
                powerCheckSwitchButton.setChecked(false);
                powerCheckSwitchButton.setEnabled(false);
                powerCheckSwitchButton.setText("Tangira gucunga");

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
            }else{
                setContentView(R.layout.activity_main_receiver);
            }
        }else{
            setContentView(R.layout.activity_main);
        }
        requestIgnoreBatteryOptimizations();
        checkForPermissions();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        } else {
            if(offlineStorageService.getDeviceSettings() != null && offlineStorageService.getDeviceSettings().isInitialized() && offlineStorageService.getDeviceSettings().isMonitor()){

                powerCheckSwitchButton.setEnabled(true);
                Log.d("MainActivity", "Battery optimization permission granted");
            }
        }

        ShowInitialSettings();
    }
    private void checkForPermissions(){
        try{


            Handler handler = new Handler(Looper.getMainLooper());

            if(!readSMSPermissionGranted){

                handler.post(() -> {

                    //Log.d("MainActivity", "Checking for Read SMS permission...");
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        if(!requestedReadSMSPermission){
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS_PERMISSION);
                            requestedReadSMSPermission = true;
                        }
                    }else{
                        readSMSPermissionGranted = true;
                        //Log.d("MainActivity", "Read SMS permission granted");
                    }
                });
            }


            if(!readPhoneStatePermissionGranted){
                handler.postDelayed(() -> {
                    //Log.d("MainActivity", "Checking for Read Phone State permission...");

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        if (!requestedReadPhoneStatePermission) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                            requestedReadPhoneStatePermission = true;
                        }
                    }else{
                        readPhoneStatePermissionGranted = true;
                        //Log.d("MainActivity", "Read Phone State permission granted");
                    }
                }, 2000); // Delay for 1 second
            }
            if(!sendSMSPermissionGranted){
                handler.postDelayed(() -> {
                    //Log.d("MainActivity", "Checking for send SMS permission...");
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        if(!requestedSendSMSPermission){
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS_PERMISSION);
                            requestedSendSMSPermission = true;
                        }
                    }else{
                        sendSMSPermissionGranted = true;
                        //Log.d("MainActivity", "Send SMS permission granted");
                    }
                }, 4000); // Delay for 2 seconds
            }

        }catch (Exception exception){
            Log.d("MainActivity", exception.getMessage());
        }

    }
    private void chooseInitSettingsDialog(){
        // Make sure one settings dialog is displayed.
        if(initialSettingsDialog != null) return;
        // Display a dialog to ask for settings
        // Save the settings
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_initial_settings, null);
        EditText userNameInput = dialogView.findViewById(R.id.userName1);
        EditText userPhoneInput = dialogView.findViewById(R.id.userPhone1);
        EditText userNameInput2 = dialogView.findViewById(R.id.userName2);
        EditText userPhoneInput2 = dialogView.findViewById(R.id.userPhone2);
        EditText userNameInput3 = dialogView.findViewById(R.id.userName3);
        EditText userPhoneInput3 = dialogView.findViewById(R.id.userPhone3);
        EditText userNameInput4 = dialogView.findViewById(R.id.userName4);
        EditText userPhoneInput4 = dialogView.findViewById(R.id.userPhone4);

        Button monitorButton = dialogView.findViewById(R.id.monitorButton);
        Button recevierButton = dialogView.findViewById(R.id.receiverButton);
        Button addMoreUsersButton = dialogView.findViewById(R.id.addMoreUsersButton);
        Button okButton = dialogView.findViewById(R.id.okButton);

        LinearLayout invalidNameSection = dialogView.findViewById(R.id.invalid_name_section);
        invalidNameSection.setVisibility(View.GONE);
        TextView invalidNameText = invalidNameSection.findViewById(R.id.AlertTitle);
        invalidNameText.setText("Andika izina, Ningombwa.");

        LinearLayout invalidNumberSection = dialogView.findViewById(R.id.invalid_number_section);
        invalidNumberSection.setVisibility(View.GONE);
        TextView invalidNumberText = invalidNumberSection.findViewById(R.id.AlertTitle);
        invalidNumberText.setText("Andika Nimero ningombwa. 07....");

        LinearLayout invalidNumberSection2 = dialogView.findViewById(R.id.invalid_number_section2);
        invalidNumberSection2.setVisibility(View.GONE);
        TextView invalidNumberText2 = invalidNumberSection2.findViewById(R.id.AlertTitle);
        invalidNumberText2.setText("Andika Nimero ningombwa. 07....");

        LinearLayout invalidNumberSection3 = dialogView.findViewById(R.id.invalid_number_section3);
        invalidNumberSection3.setVisibility(View.GONE);
        TextView invalidNumberText3 = invalidNumberSection3.findViewById(R.id.AlertTitle);
        invalidNumberText3.setText("Andika Nimero ningombwa. 07....");

        LinearLayout invalidNumberSection4 = dialogView.findViewById(R.id.invalid_number_section4);
        invalidNumberSection4.setVisibility(View.GONE);
        TextView invalidNumberText4 = invalidNumberSection4.findViewById(R.id.AlertTitle);
        invalidNumberText4.setText("Andika Nimero ningombwa. 07....");

        LinearLayout usersToNotifySection = dialogView.findViewById(R.id.usersToNotifySection);
        LinearLayout moreUsersToNotifySection = dialogView.findViewById(R.id.moreUsersToNotifySection);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable icon = getResources().getDrawable(R.drawable.check_circle_white);
        monitorButton.setOnClickListener(v -> {
            deviceSettings.setIsMonitor(true);
            isChoosing = true;

            monitorButton.setBackgroundColor(getResources().getColor(R.color.black));
            monitorButton.setTextColor(getResources().getColor(R.color.white));
            monitorButton.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

            recevierButton.setBackgroundColor(getResources().getColor(R.color.color_button_primary));
            recevierButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            recevierButton.setTextColor(getResources().getColor(R.color.black));
            ShowUsersToNotify(usersToNotifySection);
        });
        recevierButton.setOnClickListener(v -> {
            isChoosing = true;
            deviceSettings.setIsMonitor(false);

            recevierButton.setBackgroundColor(getResources().getColor(R.color.black));
            recevierButton.setTextColor(getResources().getColor(R.color.white));
            recevierButton.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

            monitorButton.setBackgroundColor(getResources().getColor(R.color.color_button_primary));
            monitorButton.setTextColor(getResources().getColor(R.color.black));
            monitorButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            ShowUsersToNotify(usersToNotifySection);
        });
        addMoreUsersButton.setOnClickListener(v -> {
            moreUsersToNotifySection.setVisibility(View.VISIBLE);
            addMoreUsersButton.setVisibility(View.GONE);
        });

        okButton.setOnClickListener(v -> {
            // check for permissions
            checkForPermissions();
            //first turn off all errors
            invalidNameSection.setVisibility(View.GONE);
            invalidNumberSection.setVisibility(View.GONE);
            invalidNumberSection2.setVisibility(View.GONE);
            invalidNumberSection3.setVisibility(View.GONE);
            invalidNumberSection4.setVisibility(View.GONE);


            if(deviceSettings.isMonitor()){
                String name = (userNameInput.getText() != null) ? userNameInput.getText().toString() : "";
                String phone = (userPhoneInput.getText() != null) ? userPhoneInput.getText().toString() : "";
                String phone2 = (userPhoneInput2.getText() != null) ? userPhoneInput2.getText().toString() : "";
                String phone3 = (userPhoneInput3.getText() != null) ? userPhoneInput3.getText().toString() : "";
                String phone4 = (userPhoneInput4.getText() != null) ? userPhoneInput4.getText().toString() : "";
                if(name.isEmpty()){
                    invalidNameSection.setVisibility(View.VISIBLE);
                    return;
                }
                if(phone.length() != 10){
                    invalidNumberSection.setVisibility(View.VISIBLE);
                    return;
                }
                if(!phone.startsWith("072") && !phone.startsWith("078") && !phone.startsWith("073") && !phone.startsWith("079")){
                    invalidNumberSection.setVisibility(View.VISIBLE);
                    return;
                }
                deviceSettings.setPhoneNumberOwner1(name);
                deviceSettings.setPhoneNumber1(phone);

                deviceSettings.setPhoneNumberOwner2(userNameInput2.getText().toString());
                deviceSettings.setPhoneNumber2(userPhoneInput2.getText().toString());

                if(!phone2.isEmpty()){
                    if(!phone2.startsWith("072") && !phone2.startsWith("078") && !phone2.startsWith("073") && !phone2.startsWith("079")){
                        invalidNumberSection2.setVisibility(View.VISIBLE);
                        return;
                    }
                }

                deviceSettings.setPhoneNumberOwner3(userNameInput3.getText().toString());
                deviceSettings.setPhoneNumber3(userPhoneInput3.getText().toString());


                if(!phone3.isEmpty()){
                    if(!phone3.startsWith("072") && !phone3.startsWith("078") && !phone3.startsWith("073") && !phone3.startsWith("079")){
                        invalidNumberSection3.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                deviceSettings.setPhoneNumberOwner4(userNameInput4.getText().toString());
                deviceSettings.setPhoneNumber4(userPhoneInput4.getText().toString());


                if(!phone4.isEmpty()){
                    if(!phone4.startsWith("072") && !phone4.startsWith("078") && !phone4.startsWith("073") && !phone4.startsWith("079")){
                        invalidNumberSection4.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }else{
                try{
                    deviceSettings.setPhoneNumber1("");
                    deviceSettings.setPhoneNumberOwner1("");
                    deviceSettings.setPhoneNumber2("");
                    deviceSettings.setPhoneNumberOwner2("");
                    deviceSettings.setPhoneNumber3("");
                    deviceSettings.setPhoneNumberOwner3("");
                    deviceSettings.setPhoneNumber4("");
                    deviceSettings.setPhoneNumberOwner4("");
                }catch (Exception ex){
                    Log.e("DeviceSettings", ex.getMessage());
                }

            }

            if(isChoosing && offlineStorageService != null){
                deviceSettings.setInitialized(true);
                offlineStorageService.createDeviceSettings(deviceSettings);
                initialSettingsDialog.dismiss();
                initialSettingsDialog = null;
                Toast.makeText(this, "Murakoze! Habitswe igenamiterere.", Toast.LENGTH_LONG).show();

                if(deviceSettings.isMonitor()){
                    setContentView(R.layout.activity_main_monitor);
                    startChargingService();

                }else{
                    setContentView(R.layout.activity_main_receiver);
                    startSMSCheckerService();
                }
            }else{
                Toast.makeText(this, "Ningombwa ko wuzuza igenamiterere.", Toast.LENGTH_LONG).show();
            }
        });
        builder.setView(dialogView);
        builder.setCancelable(false);
        initialSettingsDialog = builder.create();
        initialSettingsDialog.show();
    }

    @SuppressLint("UnsafeIntentLaunch")
    private void ShowInitialSettings(){
        // check if device has permissions if not then ask for them
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> {
            if (!readPhoneStatePermissionGranted) {
                checkForPermissions();
            }
        });

        handler.postDelayed(() -> {
            if (!readSMSPermissionGranted) {
                checkForPermissions();
            }
        }, 1000); // Delay for 1 second

        handler.postDelayed(() -> {
            if (!sendSMSPermissionGranted) {
                checkForPermissions();
            }
        }, 2000); // Delay for 2 seconds

        // check if device has settings if not then ask for them
        // get saved settings
        DeviceSettings savedSettings = offlineStorageService.getDeviceSettings();
        if(savedSettings == null){
            // display a custom dialog, with OK button to ask for settings

            if (readPhoneStatePermissionGranted && readSMSPermissionGranted && sendSMSPermissionGranted) {


                Intent intent = getIntent();
                savedSettings =  new DeviceSettings();
                savedSettings.setPermissionsGranted(true);
                deviceSettings = savedSettings;
                offlineStorageService.createDeviceSettings(savedSettings);
                assert intent != null;
                finish();
                startActivity(intent);
            }

        }else{
            //Log.d("DeviceSettings", "Device settings found" + new Gson().toJson(savedSettings).toString());
            if(savedSettings.isPermissionsGranted() && !savedSettings.isInitialized()){
                chooseInitSettingsDialog();
            }
        }
    }
    private void ShowUsersToNotify(LinearLayout usersToNotifySection){

        if(isChoosing && deviceSettings.isMonitor()){
            // show add phone number section
            usersToNotifySection.setVisibility(View.VISIBLE);
        }else{
            // hide add phone number section
            usersToNotifySection.setVisibility(View.GONE);
        }
    }

    private void requestIgnoreBatteryOptimizations() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            }else{
                //Log.d("BatteryOptimizations", "Battery optimization permission granted");
            }
        }
    }
    int readSMSFlag = 0;
    int readPhoneStateFlag = 0;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                powerCheckSwitchButton.setEnabled(true);
            } else {
                // Permission denied, handle accordingly
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);

                // force exit the app
                //Log.d("RuntimePermission", "Battery optimization permission denied, Stop the app.");
                this.finishAffinity();
            }
        }else if(requestCode == REQUEST_READ_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission denied, request the permission again

                if(ActivityCompat.shouldShowRequestPermissionRationale(this,  Manifest.permission.READ_SMS)){
                    //Toast.makeText(this, "Permission denied. Please allow permissions to proceed.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS_PERMISSION);

                }else{
                    //Toast.makeText(this, "Permission denied with never ask again. Please allow permissions from settings.", Toast.LENGTH_SHORT).show();
                    readSMSFlag++;
                    if(readSMSFlag  == 1){
                        new AlertDialog.Builder(this)
                                .setTitle("Ubusabe burakenewe")
                                .setMessage("Kureba ubutumwa bugufi birakenewe kugirango porogaramu ikore neza. Emeza ubusabe mu igenamiterere.")
                                .setPositiveButton("Komeza", (dialog, which) -> {

                                    // open app settings
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                })
                                .setNegativeButton("Reka", (dialog, which) -> {
                                    //Log.d("RuntimePermission", "Read SMS permission denied, stopping the app.");
                                    clearAppData();
                                    this.finishAffinity();
                                })
                                .setCancelable(false)
                                .show();
                    }
                }

            }else{
                readSMSPermissionGranted = true;
                ShowInitialSettings();
                Log.d("RuntimePermission", "Read SMS permission granted");
            }
        }else if(requestCode == REQUEST_SEND_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission denied, handle accordingly
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS_PERMISSION);

                Log.d("RuntimePermission", "Send SMS permission denied, stopping the app.");
                //this.finishAffinity();
            }else{
                sendSMSPermissionGranted = true;
                ShowInitialSettings();
                //Log.d("RuntimePermission", "Send SMS permission granted");
            }
        }else if(requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission denied, handle accordingly
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,  Manifest.permission.READ_PHONE_STATE)){
                    //Toast.makeText(this, "Permission denied. Please allow permissions to proceed.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);

                }else{
                    //Toast.makeText(this, "Permission denied with never ask again. Please allow permissions from settings.", Toast.LENGTH_SHORT).show();
                    readPhoneStateFlag++;
                    if(readPhoneStateFlag  == 1){
                        new AlertDialog.Builder(this)
                                .setTitle("Ubusabe burakenewe")
                                .setMessage("Kureba Telefoni birakenewe kugirango porogaramu ikore neza. Emeza ubusabe mu igenamiterere.")
                                .setPositiveButton("Komeza", (dialog, which) -> {

                                    // open app settings
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                })
                                .setNegativeButton("Reka", (dialog, which) -> {
                                    //Log.d("RuntimePermission", "Read Phone State permission denied, stopping the app.");
                                    clearAppData();
                                    this.finishAffinity();
                                })
                                .setCancelable(false)
                                .show();
                    }
                }


            }else{
                readPhoneStatePermissionGranted = true;
                ShowInitialSettings();
                //Log.d("RuntimePermission", "Read Phone State permission granted");
            }
        }
    }

    private void clearAppData(){
        try {
            // clearing app data
            if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT){
                ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            }else{
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
                /*// restart the app
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void startChargingService() {
        Intent intent = new Intent(this, com.niyonkuruelisa.umuriro.services.ChargingService.class);
        ContextCompat.startForegroundService(this, intent);
        Log.d("ChargingService", "Service created outside.");
    }
    private void startSMSCheckerService() {
/*        Intent intent = new Intent(this, com.niyonkuruelisa.umuriro.services.SMSCheckerService.class);
        ContextCompat.startForegroundService(this, intent);
        Log.d("SMSCheckerService", "Service created outside.");*/
        Log.d("SMSCheckerService", "Service created outside.");
        Intent intent = new Intent(this, com.niyonkuruelisa.umuriro.services.SMSCheckerService.class);
        startService(intent);
    }

    // on app focused again from background
    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "Murakaza neza!", Toast.LENGTH_LONG).show();
    }
}