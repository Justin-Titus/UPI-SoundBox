package com.yourpackage.upisoundbox.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.yourpackage.upisoundbox.database.TransactionDatabase;
import com.yourpackage.upisoundbox.models.Transaction;
import com.yourpackage.upisoundbox.utils.TTSHelper;
import com.yourpackage.upisoundbox.utils.UPIParser;
import java.util.concurrent.Executors;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    private TTSHelper ttsHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("UPISettings", Context.MODE_PRIVATE);
        boolean smsEnabled = prefs.getBoolean("sms_enabled", true);

        if (!smsEnabled) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    String messageBody = smsMessage.getDisplayMessageBody();

                    Log.d(TAG, "SMS from: " + sender + ", Message: " + messageBody);

                    if (UPIParser.isUPIMessage(messageBody)) {
                        processSMSMessage(context, sender, messageBody);
                    }
                }
            }
        }
    }

    private void processSMSMessage(Context context, String sender, String message) {
        UPIParser.ParseResult result = UPIParser.parseUPIMessage(message, sender);

        if (result.isValid && "received".equals(result.type)) {
            // Save to database
            TransactionDatabase db = TransactionDatabase.getDatabase(context);
            Transaction transaction = new Transaction(
                    result.amount,
                    sender,
                    UPIParser.getCurrentTimestamp(),
                    result.type,
                    "sms",
                    message
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                db.transactionDao().insert(transaction);
            });

            // Speak the amount
            if (ttsHelper == null) {
                ttsHelper = new TTSHelper(context);
            }
            ttsHelper.speakAmount(result.amount);

            Log.d(TAG, "UPI transaction processed: " + result.amount);
        }
    }
}