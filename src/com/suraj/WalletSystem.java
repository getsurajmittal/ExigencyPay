package com.suraj;
import java.util.Scanner;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class WalletSystem {
    private static Scanner sc = new Scanner(System.in);
    private static HashMap<String, User> users = new HashMap<>();
    private static User currentUser = null; // maintain logged-in user
    private static long lastActivityTime = 0; // stores last active timestamp
    private static final long SESSION_TIMEOUT = 2 * 60 * 1000; // 2 minutes in ms
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        loadUsersFromFile();

        while (true){
            System.out.println("\n Welcome to Exigency Pay");
            if (currentUser != null) {
                System.out.println("[Logged in as " + currentUser.getUserName() + "]");
            }
            System.out.println("1. Create User");
            System.out.println("2. View User Balance");
            System.out.println("3. Transfer Money");
            System.out.println("4. View Transactions");
            System.out.println("5. Deposit Money");
            System.out.println("6. Withdraw Money");
            System.out.println("7. Logout");
            System.out.println("8. Exit");
            System.out.println("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (currentUser != null && isSessionExpired()) {
                System.out.println("⚠️ Session expired. Please log in again.");
                currentUser = null;
            }

            switch (choice){
                case 1 -> createUser();
                case 2 -> viewBalance();
                case 3 -> transferMoney();
                case 4 -> viewTransactions();
                case 5 -> depositMoney();
                case 6 -> withdrawMoney();
                case 7 -> {
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                }
                case 8 -> {
                    System.out.println("Thank you for choosing ExigencyPay. Have a nice day.");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void depositMoney() {
        User user = login();
        refreshSession();
        if (user == null) return;

        System.out.print("Enter amount to deposit: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        user.updateBalance(amount);

        // record transaction
        user.addTransaction(new Transaction("DEPOSIT", amount, "SELF"));

        saveUsersToFile();
        System.out.println("Rs." + amount + " deposited successfully. New balance = Rs." + user.getBalance());
    }

    private static void withdrawMoney() {
        User user = login();
        refreshSession();
        if (user == null) return;

        System.out.print("Enter amount to withdraw: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        if (user.getBalance() < amount) {
            System.out.println("Insufficient funds.");
            return;
        }

        user.updateBalance(-amount);

        // record transaction
        user.addTransaction(new Transaction("WITHDRAW", amount, "SELF"));

        saveUsersToFile();
        System.out.println("Rs." + amount + " withdrawn successfully. New balance = Rs." + user.getBalance());
    }


    private static void refreshSession() {
        if (currentUser != null) {
            lastActivityTime = System.currentTimeMillis();
        }
    }

    private static boolean isSessionExpired() {
        if (currentUser == null) return true;
        long now = System.currentTimeMillis();
        return (now - lastActivityTime) > SESSION_TIMEOUT;
    }

    private static String generateOtp() {
        int otp = 100000 + (int)(Math.random() * 900000); // 6-digit random
        return String.valueOf(otp);
    }

    private static String now() {
        return LocalDateTime.now().format(DTF);
    }

    private static String txnId() {
        // short, readable id
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
        if (currentUser != null && !isSessionExpired()) {
            // Already logged in
            return currentUser;
        }

        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = users.get(userId);
        if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
            // ✅ Password matched → generate OTP
            String otp = generateOtp();
            System.out.println("Your OTP is: " + otp); // simulate sending via SMS/Email

            System.out.print("Enter OTP: ");
            String enteredOtp = sc.nextLine();

            if (otp.equals(enteredOtp)) {
                currentUser = user;
                refreshSession();
                System.out.println("Login successful. Welcome " + user.getUserName() + "!");
                return user;
            } else {
                System.out.println("❌ Invalid OTP. Login failed.");
                return null;
            }
        } else {
            System.out.println("❌ Invalid User ID or Password.");
            return null;
        }
    }


    private static void viewBalance(){
        User user = login();
        if (user != null) {
            System.out.println("Balance = Rs." + user.getBalance());
        }
        refreshSession();
    }

    private static void transferMoney(){
        User sender = login();  // sender must log in
        refreshSession();
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

        // Record transactions (with ID + timestamp)
        String id = txnId();
        String ts = now();

        sender.addTransaction(new Transaction("SEND", amount, receiver.getUserName()));
        receiver.addTransaction(new Transaction("RECEIVE", amount, sender.getUserName()));

        saveUsersToFile();
    }

    private static void viewTransactions(){
        User user = login();  // must log in first
        refreshSession();
        if (user != null) {
            System.out.println("Transactions for " + user.getUserName() + ":");
            if (user.getTransactionHistory().isEmpty()) {
                System.out.println("- No transactions yet.");
                return;
            }
            for (Transaction txn : user.getTransactionHistory()) {
                System.out.println("- " + txn);
            }
        }
    }

    private static final String FILE_NAME = "users.txt";
    
    private static void saveUsersToFile() {
        try (PrintWriter writer = new PrintWriter(FILE_NAME)) {
            String json = gson.toJson(users);
            writer.println(json);
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    private static void loadUsersFromFile() {
        users.clear();
        try (Scanner fileScanner = new Scanner(new java.io.File(FILE_NAME))) {
            if (fileScanner.hasNextLine()) {
                String json = fileScanner.useDelimiter("\\Z").next(); // read entire file
                users = gson.fromJson(json, new TypeToken<HashMap<String, User>>(){}.getType());
            }
        } catch (Exception e) {
            System.out.println("No saved users found (first time use).");
        }
    }
}
