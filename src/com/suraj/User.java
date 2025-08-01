package com.suraj;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String userName;
    private String userId;
    private double balance;
    private List<String> transactionHistory;

    public User(String userName, String userId, double balance){
        this.userName=userName;
        this.userId=userId;
        this.balance=balance;
        this.transactionHistory=new ArrayList<>();
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
}
