package model;
public class Deposit {
    private int id;
    private int customerId;
    private double totalDeposit;
    private String depositDate;

    public Deposit(int id, int customerId, double totalDeposit, String depositDate) {
        this.id = id;
        this.customerId = customerId;
        this.totalDeposit = totalDeposit;
        this.depositDate = depositDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public double getTotalDeposit() { return totalDeposit; }
    public void setTotalDeposit(double totalDeposit) { this.totalDeposit = totalDeposit; }

    public String getDepositDate() { return depositDate; }
    public void setDepositDate(String depositDate) { this.depositDate = depositDate; }
}
