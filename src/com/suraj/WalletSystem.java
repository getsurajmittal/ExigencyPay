package com.suraj;
import java.util.Scanner;
import java.util.HashMap;

public class WalletSystem {
    private static Scanner sc = new Scanner(System.in);
    private static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        while (true){
            System.out.println("\n Welcome to Exigency Pay");
            System.out.println("1. Create User");
            System.out.println("2. View User Balance");
            System.out.println("3. Transfer Money");
            System.out.println("4. View Transactions");
            System.out.println("5. Exit");
            System.out.println("Choose option: ");
            
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice){
                case 1 -> createUser();
                case 2 -> viewBalance();
                case 3 -> transferMoney();
                case 4 -> viewTransactions();
                case 5 -> {
                    System.out.println("Thank you for choosing ExigencyPay. Have a nice day.");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void createUser(){
        System.out.println("Enter UserId: ");
        String userId = sc.nextLine();
        System.out.println("Enter Name: ");
        String userName = sc.nextLine();
        System.out.println("Enter Initial Balance: ");
        double balance = sc.nextDouble();
        sc.nextLine();

        User user = new User(userName,userId,balance);
        users.put(userId,user);
        System.out.println("User created Successfully.");
    }

    private static void viewBalance(){
        System.out.println("Enter User Id: ");
        String userId = sc.nextLine();
        User user = users.get(userId);

        if (user != null){
            System.out.println("Balance = Rs." + user.getBalance());
        }
        else{
            System.out.println("User not found.");
        }
    }

    private static void transferMoney(){
        System.out.println("Enter sender userId: ");
        String senderUserId = sc.nextLine();
        System.out.println("Enter receiver userId: ");
        String receiverUserId = sc.nextLine();
        System.out.println("Enter the amount to transfer: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        User sender = users.get(senderUserId);
        User receiver = users.get(receiverUserId);

        if (sender == null){
            System.out.println("Sender user not found.");
            return;
        }
        if (receiver == null){
            System.out.println("Receiver user not found.");
            return;
        }

        if (sender.getBalance() < amount){
            System.out.println("Insufficient Funds");
            return;
        }

        sender.updateBalance(-amount);
        receiver.updateBalance(amount);

        String transaction = "Transferred Rs." + amount + " from " + sender.getUserName() + " to " + receiver.getUserName();
        sender.addTransaction("Sent Rs." + amount + " to " + receiver.getUserName());
        receiver.addTransaction("Received Rs." + amount + " from " + sender.getUserName() );

        System.out.println(transaction);
    }

    private static void viewTransactions(){
        System.out.println("Enter User Id: ");
        String userId = sc.nextLine();
        User user = users.get(userId);

        if (user != null){
            System.out.println("Transactions for " + user.getUserName() + " : ");
            for(String txn : user.getTransactionHistory()){
                System.out.println("- " + txn);
            }
        }
        else{
            System.out.println("User not found.");
        }
    }
}
