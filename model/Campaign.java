package model;
public class Campaign {
    private int id;
    private double depositAmount;
    private double bonusAmount;

    /**
     * Design by Contract: Campaign Constructor
     * @requires id > 0 && depositAmount > 0 && bonusAmount >= 0
     * @ensures getId() == id && getDepositAmount() == depositAmount && getBonusAmount() == bonusAmount
     */
    public Campaign(int id, double depositAmount, double bonusAmount) {
        // Preconditions
        assert id > 0 : "Campaign ID must be positive";
        assert depositAmount > 0 : "Deposit amount must be positive";
        assert bonusAmount >= 0 : "Bonus amount cannot be negative";
        
        this.id = id;
        this.depositAmount = depositAmount;
        this.bonusAmount = bonusAmount;
        
        // Postconditions
        assert this.id == id : "Campaign ID not set correctly";
        assert this.depositAmount == depositAmount : "Deposit amount not set correctly";
        assert this.bonusAmount == bonusAmount : "Bonus amount not set correctly";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(double depositAmount) { this.depositAmount = depositAmount; }

    public double getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(double bonusAmount) { this.bonusAmount = bonusAmount; }
}
