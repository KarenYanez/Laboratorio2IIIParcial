package ec.edu.espe.buildtextci.model;

import java.util.UUID;

public class Wallet {

    private final String id;
    private final String ownerEmail;
    private double balance;

    // Constructor (id automático)
    public Wallet(String ownerEmail, double balance) {
        this.id = UUID.randomUUID().toString();
        this.ownerEmail = ownerEmail;
        this.balance = balance;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public double getBalance() {
        return balance;
    }

    // Setter solo para balance
    public void setBalance(double balance) {
        this.balance = balance;
    }

    //Depositar dinero
    public void deposit(double amount){
        this.balance += amount;
    }

    //retirar dinero si existe saldo suficiente
    public void withdraw(double amount) {
        this.balance -= amount;
    }
}