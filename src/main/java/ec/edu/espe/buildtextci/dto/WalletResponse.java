package ec.edu.espe.buildtextci.dto;

public class WalletResponse{

    private String walletId;
    private double balance;

    // Constructor
    public WalletResponse(String walletId, double balance) {
        this.walletId = walletId;
        this.balance = balance;
    }

    // Getters
    public String getWalletId() {
        return walletId;
    }

    public double getBalance() {
        return balance;
    }

    // Setters
    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}