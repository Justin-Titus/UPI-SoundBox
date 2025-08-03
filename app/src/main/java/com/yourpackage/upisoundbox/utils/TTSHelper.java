package com.yourpackage.upisoundbox.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class TTSHelper {
    private static final String TAG = "TTSHelper";
    private TextToSpeech textToSpeech;
    private boolean isInitialized = false;
    private String queuedText = null;

    public TTSHelper(Context context) {
        textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = new Locale("en", "IN");
                int result = textToSpeech.setLanguage(locale);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "TTS Language (en-IN) not supported, falling back to US English.");
                    textToSpeech.setLanguage(Locale.US);
                } else {
                    isInitialized = true;
                    Log.d(TAG, "TTS initialized successfully");
                    if (queuedText != null) {
                        speak(queuedText);
                        queuedText = null;
                    }
                }
            } else {
                Log.e(TAG, "TTS initialization failed");
            }
        });
    }

    // --- NEW, SAFER HELPER METHOD ---
    /**
     * Formats an amount string by removing trailing ".00" or ".0" if they exist.
     * This avoids mathematical conversions that can alter TTS pronunciation.
     * - "1653.00" -> "1653"
     * - "50.0"    -> "50"
     * - "120.50"  -> "120.50" (unchanged)
     * - "1653"    -> "1653" (unchanged)
     *
     * @param amountString The amount as a string.
     * @return A cleaned-up string suitable for TTS.
     */
    private String formatAmountForSpeech(String amountString) {
        if (amountString == null || amountString.isEmpty()) {
            return "";
        }

        // Check for ".00" first, as it's more specific
        if (amountString.endsWith(".00")) {
            return amountString.substring(0, amountString.length() - 3);
        }

        // Then check for ".0"
        if (amountString.endsWith(".0")) {
            return amountString.substring(0, amountString.length() - 2);
        }

        // If neither, return the original string unchanged
        return amountString;
    }

    public void speak(String text) {
        if (isInitialized && textToSpeech != null) {
            Log.d(TAG, "Speaking: " + text);
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.d(TAG, "TTS not initialized, queuing: " + text);
            queuedText = text;
        }
    }

    // --- speakAmount METHOD (uses the new safer formatter) ---
    public void speakAmount(String amount) {
        // Use the safe string manipulation method
        String formattedAmount = formatAmountForSpeech(amount);
        String message = "Payment received rupees " + formattedAmount;
        speak(message);
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}