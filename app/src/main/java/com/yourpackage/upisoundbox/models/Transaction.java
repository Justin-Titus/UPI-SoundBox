package com.yourpackage.upisoundbox.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String amount;
    public String sender;
    public String timestamp;
    public String type; // "received" or "sent"
    public String source; // "sms" or "notification"
    public String rawMessage;

    // Constructors
    public Transaction() {}

    public Transaction(String amount, String sender, String timestamp, String type, String source, String rawMessage) {
        this.amount = amount;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.source = source;
        this.rawMessage = rawMessage;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getRawMessage() { return rawMessage; }
    public void setRawMessage(String rawMessage) { this.rawMessage = rawMessage; }
}