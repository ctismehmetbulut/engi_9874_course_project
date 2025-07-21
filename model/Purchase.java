package model;
public class Purchase {
    private int id;
    private int customerId;
    private String productName;
    private double productPrice;
    private String purchaseDate;

    public Purchase(int id, int customerId, String productName, double productPrice, String purchaseDate) {
        this.id = id;
        this.customerId = customerId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.purchaseDate = purchaseDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
}
