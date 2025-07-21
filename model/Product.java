package model;
public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;

    /**
     * Design by Contract: Product Constructor
     * @requires id > 0 && name != null && !name.trim().isEmpty() && price > 0 && stock >= 0
     * @ensures getId() == id && getName().equals(name) && getPrice() == price && getStock() == stock
     */
    public Product(int id, String name, double price, int stock) {
        // Preconditions
        assert id > 0 : "Product ID must be positive";
        assert name != null && !name.trim().isEmpty() : "Product name cannot be null or empty";
        assert price > 0 : "Product price must be positive";
        assert stock >= 0 : "Product stock cannot be negative";
        
        this.id = id;
        this.name = name.trim();
        this.price = price;
        this.stock = stock;
        
        // Postconditions
        assert this.id == id : "Product ID not set correctly";
        assert this.name.equals(name.trim()) : "Product name not set correctly";
        assert this.price == price : "Product price not set correctly";
        assert this.stock == stock : "Product stock not set correctly";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    /**
     * Design by Contract: Set Price
     * @requires price > 0
     * @ensures getPrice() == price
     */
    public void setPrice(double price) { 
        // Precondition
        assert price > 0 : "Price must be positive";
        
        this.price = price; 
        
        // Postcondition
        assert this.price == price : "Price not updated correctly";
    }

    public int getStock() { return stock; }
    /**
     * Design by Contract: Set Stock
     * @requires stock >= 0
     * @ensures getStock() == stock
     */
    public void setStock(int stock) { 
        // Precondition
        assert stock >= 0 : "Stock cannot be negative";
        
        this.stock = stock; 
        
        // Postcondition
        assert this.stock == stock : "Stock not updated correctly";
    }
}
