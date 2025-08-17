package com.suraj;
import java.util.Scanner;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.HashMap;

public class WalletSystem {
    private static Scanner sc = new Scanner(System.in);
    private static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        loadUsersFromFile();

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

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
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
        System.out.println("Enter a password (PIN): ");
        String password = sc.nextLine();

        User user = new User(userName,userId,balance, hashPassword(password));
        users.put(userId,user);
        System.out.println("User created Successfully.");
        saveUsersToFile();
    }

    private static User login() {
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = users.get(userId);
        if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
            System.out.println("Login successful. Welcome " + user.getUserName() + "!");
            return user;
        } else {
            System.out.println("Invalid User ID or Password.");
            return null;
        }
    }

    private static void viewBalance(){
        User user = login();
        if (user != null) {
            System.out.println("Balance = Rs." + user.getBalance());
        }
    }

    private static void transferMoney(){
        User sender = login();  // sender must log in
        if (sender == null) return;

        System.out.println("Enter receiver userId: ");
        String receiverUserId = sc.nextLine();
        User receiver = users.get(receiverUserId);

        if (receiver == null) {
            System.out.println("Receiver user not found.");
            return;
        }

        System.out.println("Enter the amount to transfer: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        if (sender.getBalance() < amount) {
            System.out.println("Insufficient Funds");
            return;
        }

        // Perform transfer
        sender.updateBalance(-amount);
        receiver.updateBalance(amount);

        // Record transactions
        sender.addTransaction("Sent Rs." + amount + " to " + receiver.getUserName());
        receiver.addTransaction("Received Rs." + amount + " from " + sender.getUserName());

        System.out.println("Transferred Rs." + amount + " from " + sender.getUserName() +
                        " to " + receiver.getUserName());

        saveUsersToFile();
    }

    private static void viewTransactions(){
        User user = login();  // must log in first
        if (user != null) {
            System.out.println("Transactions for " + user.getUserName() + ":");
            for (String txn : user.getTransactionHistory()) {
                System.out.println("- " + txn);
            }
        }
    }

    private static final String FILE_NAME = "users.txt";
    
    private static void saveUsersToFile() {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            for (User user : users.values()) {
                writer.println(user.toFileString());
            }
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    private static void loadUsersFromFile() {
        users.clear();
        try (Scanner fileScanner = new Scanner(new java.io.File(FILE_NAME))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                User user = User.fromFileString(line);
                users.put(user.getUserId(), user);
            }
        } catch (Exception e) {
            System.out.println("No saved users found (first time use).");
        }
    }
}
