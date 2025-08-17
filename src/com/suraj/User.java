package com.suraj;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String userName;
    private String userId;
    private double balance;
    private List<String> transactionHistory;
    private String passwordHash;

    public User(String userName, String userId, double balance, String passwordHash) {
        this.userName=userName;
        this.userId=userId;
        this.balance=balance;
        this.transactionHistory=new ArrayList<>();
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserId(){
        return userId;
    }

    public double getBalance(){
        return balance;
    }

    public List<String> getTransactionHistory(){
        return transactionHistory;
    }

    public void addTransaction(String transaction){
        transactionHistory.add(transaction);
    }

    public void updateBalance(double amount){
        this.balance += amount;
    }

    public String toFileString() {
        return userId + "|" + userName + "|" + balance + "|" + passwordHash + "|" + String.join(",", transactionHistory);
    }

    public static User fromFileString(String line) {
        String[] parts = line.split("\\|"); // split a string logic
        String userId = parts[0];
        String userName = parts[1];
        double balance = Double.parseDouble(parts[2]);
        String passwordHash = parts[3];
        User user = new User(userName, userId, balance, passwordHash);

        if (parts.length > 4 && !parts[4].isEmpty()) {
            String[] txns = parts[4].split(",");
            for (String txn : txns) {
                user.addTransaction(txn);
            }
        }
        return user;
    }


}
