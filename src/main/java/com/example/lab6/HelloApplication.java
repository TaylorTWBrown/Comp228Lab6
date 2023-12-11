package com.example.lab6;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Account {
    private double balance;

    public Account(double initialBalance) {
        this.balance = initialBalance;
    }

    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited: $" + amount + ", New Balance: $" + balance);
    }

    public synchronized void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println("Withdrawn: $" + amount + ", New Balance: $" + balance);
        } else {
            System.out.println("Insufficient funds for withdrawal.");
        }
    }

    public synchronized double getBalance() {
        return balance;
    }
}

class Transaction implements Runnable {
    private Account account;
    private String transactionType;
    private double amount;

    public Transaction(Account account, String transactionType, double amount) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    @Override
    public void run() {
        if ("deposit".equals(transactionType)) {
            account.deposit(amount);
        } else if ("withdraw".equals(transactionType)) {
            account.withdraw(amount);
        }
    }
}

public class HelloApplication {
    public static void main(String[] args) {
        // Create an account with initial balance
        Account account = new Account(1000.0);

        // Set up a list of transactions
        ArrayList<Runnable> transactions = new ArrayList<>();
        transactions.add(new Transaction(account, "deposit", 200.0));
        transactions.add(new Transaction(account, "withdraw", 300.0));
        transactions.add(new Transaction(account, "withdraw", 800.0));

        // Use a thread pool for concurrent execution
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Execute the transactions using the thread pool
        for (Runnable transaction : transactions) {
            executor.execute(transaction);
        }

        // Shut down the thread pool
        executor.shutdown();

        try {
            // Wait for all threads to finish
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Display the final account balance
        System.out.println("Final Balance: $" + account.getBalance());
    }
}
