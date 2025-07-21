package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import model.Product;
import model.Customer;
import model.Campaign;
import model.Deposit;
import model.Purchase;

public class MemoryStore {
    // Product and Product_Id
    public static final Map<Integer, Product> products = new ConcurrentHashMap<>();
    // Customer and Customer_Id
    public static final Map<Integer, Customer> customers = new ConcurrentHashMap<>();
    // Campaign and Campaign_Id
    public static final Map<Integer, Campaign> campaigns = new ConcurrentHashMap<>();

    // Deposits and Purchases by Customer_Id
    public static final Map<Integer, List<Deposit>> depositsByCustomer = new ConcurrentHashMap<>();
    public static final Map<Integer, List<Purchase>> purchasesByCustomer = new ConcurrentHashMap<>();

    // Utility method to add deposit
    public static void addDeposit(Deposit deposit) {
        depositsByCustomer
                .computeIfAbsent(deposit.getCustomerId(), _ -> Collections.synchronizedList(new ArrayList<>()))
                .add(0, deposit); // Add to the beginning for descending order
    }

    // Utility method to add purchase
    public static void addPurchase(Purchase purchase) {
        purchasesByCustomer
                .computeIfAbsent(purchase.getCustomerId(), _ -> Collections.synchronizedList(new ArrayList<>()))
                .add(0, purchase); // Add to the beginning for descending order
    }

    // Optional getters
    public static List<Deposit> getDepositsForCustomer(int customerId) {
        return depositsByCustomer.getOrDefault(customerId, Collections.emptyList());
    }

    public static List<Purchase> getPurchasesForCustomer(int customerId) {
        return purchasesByCustomer.getOrDefault(customerId, Collections.emptyList());
    }

    public static void loadCampaignsFromDatabase() {
        campaigns.clear();
        try (ResultSet rs = CampaignDAO.getAllCampaigns()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                double deposit = rs.getDouble("deposit_amount");
                double bonus = rs.getDouble("bonus_amount");
                campaigns.put(id, new Campaign(id, deposit, bonus));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load campaigns into memory", e);
        }
    }

    public static void loadProductsFromDatabase() {
        products.clear();
        try (ResultSet rs = ProductDAO.getAllProducts()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                products.put(id, new Product(id, name, price, stock));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load products into memory", e);
        }
    }

    public static void initializeAll() {
        loadCampaignsFromDatabase();
        loadProductsFromDatabase();
        // You can later add: loadCustomersFromDatabase() etc.
    }
}
