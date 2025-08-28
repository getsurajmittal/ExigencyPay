package com.suraj;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String type;         // "SEND" or "RECEIVE"
    private double amount;
    private String counterparty; // other user’s name
    private String timestamp;

    public Transaction(String type, double amount, String counterparty) {
        this.type = type;
        this.amount = amount;
        this.counterparty = counterparty;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getCounterparty() { return counterparty; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + type + " Rs." + amount + " " + (type.equals("SEND") ? "to " : "from ") + counterparty;
    }
}