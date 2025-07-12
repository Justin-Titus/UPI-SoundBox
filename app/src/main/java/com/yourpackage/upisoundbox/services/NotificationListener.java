package com.yourpackage.upisoundbox.services;

import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.yourpackage.upisoundbox.database.TransactionDatabase;
import com.yourpackage.upisoundbox.models.Transaction;
import com.yourpackage.upisoundbox.utils.TTSHelper;
import com.yourpackage.upisoundbox.utils.UPIParser;
import java.util.concurrent.Executors;

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    private TTSHelper ttsHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        ttsHelper = new TTSHelper(this);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        SharedPreferences prefs = getSharedPreferences("UPISettings", MODE_PRIVATE);
        boolean notificationEnabled = prefs.getBoolean("notification_enabled", false);

        if (!notificationEnabled) {
            return;
        }

        String packageName = sbn.getPackageName();
        String title = "";
        String text = "";

        if (sbn.getNotification().extras != null) {
            title = sbn.getNotification().extras.getString("android.title", "");
            text = sbn.getNotification().extras.getString("android.text", "");
        }

        String fullMessage = title + " " + text;

        Log.d(TAG, "Notification from: " + packageName + ", Message: " + fullMessage);

        if (isUPIApp(packageName) && UPIParser.isUPIMessage(fullMessage)) {
            processNotification(packageName, fullMessage);
        }
    }

    private boolean isUPIApp(String packageName) {
        return packageName.contains("paytm") ||
                packageName.contains("phonepe") ||
                packageName.contains("googlepay") ||
                packageName.contains("gpay") ||
                packageName.contains("bhim") ||
                packageName.contains("upi");
    }

    private void processNotification(String packageName, String message) {
        UPIParser.ParseResult result = UPIParser.parseUPIMessage(message, packageName);

        if (result.isValid && "received".equals(result.type)) {
            // Save to database
            TransactionDatabase db = TransactionDatabase.getDatabase(this);
            Transaction transaction = new Transaction(
                    result.amount,
                    packageName,
                    UPIParser.getCurrentTimestamp(),
                    result.type,
                    "notification",
                    message
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                db.transactionDao().insert(transaction);
            });

            // Speak the amount
            ttsHelper.speakAmount(result.amount);

            Log.d(TAG, "UPI notification processed: " + result.amount);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ttsHelper != null) {
            ttsHelper.shutdown();
        }
    }
}