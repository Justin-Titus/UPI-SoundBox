package com.yourpackage.upisoundbox.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.yourpackage.upisoundbox.R;
import com.yourpackage.upisoundbox.utils.PermissionHelper;
import com.yourpackage.upisoundbox.utils.TTSHelper;

public class MainActivity extends AppCompatActivity {

    private SwitchMaterial smsSwitch;
    private SwitchMaterial notificationSwitch;
    private Button btnSmsPermission, btnNotificationPermission, btnBatteryOptimization;
    private Button btnHistory, btnTestTTS;
    private TextView tvStatus;
    private SharedPreferences preferences;
    private TTSHelper ttsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupPreferences();
        setupListeners();
        updateUI();

        ttsHelper = new TTSHelper(this);
    }

    private void initializeViews() {
        smsSwitch = findViewById(R.id.sms_switch);
        notificationSwitch = findViewById(R.id.notification_switch);
        btnSmsPermission = findViewById(R.id.btn_sms_permission);
        btnNotificationPermission = findViewById(R.id.btn_notification_permission);
        btnBatteryOptimization = findViewById(R.id.btn_battery_optimization);
        btnHistory = findViewById(R.id.btn_history);
        btnTestTTS = findViewById(R.id.btn_test_tts);
        tvStatus = findViewById(R.id.tv_status);
    }

    private void setupPreferences() {
        preferences = getSharedPreferences("UPISettings", MODE_PRIVATE);

        // Load saved preferences
        smsSwitch.setChecked(preferences.getBoolean("sms_enabled", true));
        notificationSwitch.setChecked(preferences.getBoolean("notification_enabled", false));
    }

    private void setupListeners() {
        smsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("sms_enabled", isChecked).apply();
            updateStatus();
        });

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("notification_enabled", isChecked).apply();
            updateStatus();
        });

        btnSmsPermission.setOnClickListener(v -> {
            if (!PermissionHelper.hasSMSPermission(this)) {
                PermissionHelper.requestSMSPermission(this);
            } else {
                Toast.makeText(this, "SMS Permission already granted", Toast.LENGTH_SHORT).show();
            }
        });

        btnNotificationPermission.setOnClickListener(v -> {
            if (!PermissionHelper.isNotificationListenerEnabled(this)) {
                PermissionHelper.requestNotificationListenerPermission(this);
            } else {
                Toast.makeText(this, "Notification Access already granted", Toast.LENGTH_SHORT).show();
            }
        });

        btnBatteryOptimization.setOnClickListener(v -> {
            if (!PermissionHelper.isBatteryOptimizationDisabled(this)) {
                PermissionHelper.requestDisableBatteryOptimization(this);
            } else {
                Toast.makeText(this, "Battery Optimization already disabled", Toast.LENGTH_SHORT).show();
            }
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        btnTestTTS.setOnClickListener(v -> {
            ttsHelper.speakAmount("100.50");
        });
    }

    private void updateUI() {
        updatePermissionButtons();
        updateStatus();
    }

    private void updatePermissionButtons() {
        // Update SMS permission button
        if (PermissionHelper.hasSMSPermission(this)) {
            btnSmsPermission.setText("SMS Permission ✓");
            btnSmsPermission.setEnabled(false);
        } else {
            btnSmsPermission.setText("Grant SMS Permission");
            btnSmsPermission.setEnabled(true);
        }

        // Update notification permission button
        if (PermissionHelper.isNotificationListenerEnabled(this)) {
            btnNotificationPermission.setText("Notification Access ✓");
            btnNotificationPermission.setEnabled(false);
        } else {
            btnNotificationPermission.setText("Grant Notification Access");
            btnNotificationPermission.setEnabled(true);
        }

        // Update battery optimization button
        if (PermissionHelper.isBatteryOptimizationDisabled(this)) {
            btnBatteryOptimization.setText("Battery Optimization ✓");
            btnBatteryOptimization.setEnabled(false);
        } else {
            btnBatteryOptimization.setText("Disable Battery Optimization");
            btnBatteryOptimization.setEnabled(true);
        }
    }

    private void updateStatus() {
        String status = "Status: ";

        if (smsSwitch.isChecked() && PermissionHelper.hasSMSPermission(this)) {
            status += "SMS Active";
        } else if (notificationSwitch.isChecked() && PermissionHelper.isNotificationListenerEnabled(this)) {
            status += "Notification Active";
        } else {
            status += "Setup Required";
        }

        tvStatus.setText(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionHelper.SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Permission denied", Toast.LENGTH_SHORT).show();
            }
            updateUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ttsHelper != null) {
            ttsHelper.shutdown();
        }
    }
}