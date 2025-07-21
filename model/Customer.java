package model;
public class Customer {
    private int id;
    private String name;
    private String password;
    private double balance;

    /**
     * Design by Contract: Customer Constructor
     * @requires id > 0 && name != null && !name.trim().isEmpty() && 
     *           password != null && password.matches("\\d{4}") && balance >= 0
     * @ensures getId() == id && getName().equals(name.trim()) && 
     *          getPassword().equals(password) && getBalance() == balance
     */
    public Customer(int id, String name, String password, double balance) {
        // Preconditions
        assert id > 0 : "Customer ID must be positive";
        assert name != null && !name.trim().isEmpty() : "Customer name cannot be null or empty";
        assert password != null && password.matches("\\d{4}") : "Password must be a 4-digit PIN";
        assert balance >= 0 : "Customer balance cannot be negative";
        
        this.id = id;
        this.name = name.trim();
        this.password = password;
        this.balance = balance;
        
        // Postconditions
        assert this.id == id : "Customer ID not set correctly";
        assert this.name.equals(name.trim()) : "Customer name not set correctly";
        assert this.password.equals(password) : "Customer password not set correctly";
        assert this.balance == balance : "Customer balance not set correctly";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public double getBalance() { return balance; }
    /**
     * Design by Contract: Set Balance
     * @requires balance >= 0
     * @ensures getBalance() == balance
     */
    public void setBalance(double balance) { 
        // Precondition
        assert balance >= 0 : "Balance cannot be negative";
        
        this.balance = balance; 
        
        // Postcondition
        assert this.balance == balance : "Balance not updated correctly";
    }
}
