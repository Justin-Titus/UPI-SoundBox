package com.yourpackage.upisoundbox.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UPIParser {

    // Common UPI patterns for different banks
    private static final String[] RECEIVED_PATTERNS = {
            "(?i).*received.*rs[\\s\\.]*([0-9,]+(?:\\.[0-9]{2})?).*",
            "(?i).*credited.*rs[\\s\\.]*([0-9,]+(?:\\.[0-9]{2})?).*",
            "(?i).*Rs[\\s\\.]*([0-9,]+(?:\\.[0-9]{2})?).*(received|credited).*",
            "(?i).*([0-9,]+(?:\\.[0-9]{2})?).*(received|credited).*",
            "(?i).*INR[\\s]*([0-9,]+(?:\\.[0-9]{2})?).*received.*"
    };

    private static final String[] SENT_PATTERNS = {
            "(?i).*sent.*rs[\\s\\.]*([0-9,]+(?:\\.[0-9]{2})?).*",
            "(?i).*debited.*rs[\\s\\.]*([0-9,]+(?:\\.[0-9]{2})?).*",
            "(?i).*Rs[\\s\\.]*([0-9,]+(?:\\.[0-9]{2})?).*(sent|debited).*",
            "(?i).*([0-9,]+(?:\\.[0-9]{2})?).*(sent|debited).*"
    };

    public static class ParseResult {
        public String amount;
        public String type; // "received" or "sent"
        public String sender;
        public boolean isValid;

        public ParseResult(String amount, String type, String sender, boolean isValid) {
            this.amount = amount;
            this.type = type;
            this.sender = sender;
            this.isValid = isValid;
        }
    }

    public static ParseResult parseUPIMessage(String message, String sender) {
        String cleanAmount = null;
        String transactionType = null;

        // Check for received transactions first
        for (String pattern : RECEIVED_PATTERNS) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(message);
            if (m.find()) {
                cleanAmount = m.group(1).replaceAll(",", "");
                transactionType = "received";
                break;
            }
        }

        // If not found, check for sent transactions
        if (cleanAmount == null) {
            for (String pattern : SENT_PATTERNS) {
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(message);
                if (m.find()) {
                    cleanAmount = m.group(1).replaceAll(",", "");
                    transactionType = "sent";
                    break;
                }
            }
        }

        boolean isValid = cleanAmount != null && isValidAmount(cleanAmount);

        return new ParseResult(cleanAmount, transactionType, sender, isValid);
    }

    private static boolean isValidAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            return value > 0 && value <= 1000000; // Max 10 lakhs
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static boolean isUPIMessage(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("upi") ||
                lowerMessage.contains("paytm") ||
                lowerMessage.contains("phonepe") ||
                lowerMessage.contains("googlepay") ||
                lowerMessage.contains("gpay") ||
                lowerMessage.contains("bhim") ||
                lowerMessage.contains("received") ||
                lowerMessage.contains("credited") ||
                lowerMessage.contains("debited") ||
                lowerMessage.contains("sent");
    }
}