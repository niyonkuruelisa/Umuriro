package com.niyonkuruelisa.umuriro;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.niyonkuruelisa.umuriro.services.OfflineStorageService;

public class SettingsActivity extends AppCompatActivity {
    private AppCompatImageButton backBtn;
    private OfflineStorageService offlineStorageService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        offlineStorageService = new OfflineStorageService(this);

        // we need to check if the user is chosen to be monitor or receiver then display UI accordingly.
        if(offlineStorageService.getDeviceSettings().isMonitor()){
            // hide the receiver UI
            findViewById(R.id.receiverUI).setVisibility(android.view.View.GONE);
        }else{
            // hide the monitor UI
            findViewById(R.id.monitorUI).setVisibility(android.view.View.GONE);
        }
        backBtn = findViewById(R.id.backToMainButton);

        backBtn.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }
}