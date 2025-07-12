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
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "TTS Language not supported");
                } else {
                    isInitialized = true;
                    Log.d(TAG, "TTS initialized successfully");

                    // If something was queued before initialization
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

    public void speak(String text) {
        if (isInitialized && textToSpeech != null) {
            Log.d(TAG, "Speaking: " + text);
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.d(TAG, "TTS not initialized, queuing: " + text);
            queuedText = text;
        }
    }

    public void speakAmount(String amount) {
        String message = "Payment received of rupees " + amount;
        speak(message);
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
