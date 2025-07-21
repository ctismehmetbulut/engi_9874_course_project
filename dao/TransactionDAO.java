package dao;

import java.sql.*;

import model.Customer;
import model.Deposit;
import model.Purchase;

public class TransactionDAO {

    /**
     * Design by Contract: Deposit to Customer
     * @requires totalAmount > 0 && customerId > 0
     * @ensures \result == true implies customer balance increased by totalAmount
     */
    public static boolean depositToCustomer(double totalAmount, int customerId) {
        // Preconditions
        assert totalAmount > 0 : "Deposit amount must be positive";
        assert customerId > 0 : "Customer ID must be positive";
        
        String updateSql = "UPDATE customers SET balance = balance + ? WHERE id = ?";
        String insertSql = "INSERT INTO deposits (customer_id, total_deposit) VALUES (?, ?)";

        try (
                PreparedStatement updateStmt = Database.getConnection().prepareStatement(updateSql);
                PreparedStatement insertStmt = Database.getConnection().prepareStatement(insertSql,
                        Statement.RETURN_GENERATED_KEYS)) {
            // Update balance
            updateStmt.setDouble(1, totalAmount);
            updateStmt.setInt(2, customerId);
            updateStmt.executeUpdate();

            // Insert into deposits table
            insertStmt.setInt(1, customerId);
            insertStmt.setDouble(2, totalAmount);
            insertStmt.executeUpdate();

            int depositId = -1;
            String depositDate = null;
            try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                if (keys.next()) {
                    depositId = keys.getInt(1);
                    // Optional: fetch timestamp if needed
                    PreparedStatement stmt = Database.getConnection().prepareStatement(
                            "SELECT deposit_date FROM deposits WHERE id = ?");
                    stmt.setInt(1, depositId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        depositDate = rs.getString("deposit_date");
                    }
                }
            }

            // Update memory
            Customer customer = MemoryStore.customers.get(customerId);
            if (customer != null) {
                customer.setBalance(customer.getBalance() + totalAmount);
            }

            if (depositId != -1 && depositDate != null) {
                Deposit deposit = new Deposit(depositId, customerId, totalAmount, depositDate);
                MemoryStore.addDeposit(deposit);
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to deposit to customer", e);
        }
    }

    public static PurchaseResult purchaseProduct(String productName, double price, int customerId) {
        String checkBalanceSql = "SELECT balance FROM customers WHERE id = ?";
        String deductBalanceSql = "UPDATE customers SET balance = balance - ? WHERE id = ?";
        String insertPurchaseSql = "INSERT INTO purchases (customer_id, product_name, product_price) VALUES (?, ?, ?)";

        try (
                PreparedStatement checkBalanceStmt = Database.getConnection().prepareStatement(checkBalanceSql);
                PreparedStatement deductBalanceStmt = Database.getConnection().prepareStatement(deductBalanceSql);
                PreparedStatement insertPurchaseStmt = Database.getConnection().prepareStatement(insertPurchaseSql,
                        Statement.RETURN_GENERATED_KEYS)) {
            // Check balance
            checkBalanceStmt.setInt(1, customerId);
            ResultSet balanceRs = checkBalanceStmt.executeQuery();
            if (!balanceRs.next())
                return PurchaseResult.CUSTOMER_NOT_FOUND;

            double balance = balanceRs.getDouble("balance");
            if (balance < price)
                return PurchaseResult.INSUFFICIENT_BALANCE;

            // Get product ID and stock
            int productId = ProductDAO.getProductIdByName(productName);
            int stock = ProductDAO.getProductStock(productId);
            if (stock <= 0)
                return PurchaseResult.OUT_OF_STOCK;

            // Deduct balance
            deductBalanceStmt.setDouble(1, price);
            deductBalanceStmt.setInt(2, customerId);
            deductBalanceStmt.executeUpdate();

            // Record purchase
            insertPurchaseStmt.setInt(1, customerId);
            insertPurchaseStmt.setString(2, productName);
            insertPurchaseStmt.setDouble(3, price);
            insertPurchaseStmt.executeUpdate();

            int purchaseId = -1;
            String purchaseDate = null;
            try (ResultSet keys = insertPurchaseStmt.getGeneratedKeys()) {
                if (keys.next()) {
                    purchaseId = keys.getInt(1);
                    // Fetch timestamp
                    PreparedStatement stmt = Database.getConnection().prepareStatement(
                            "SELECT purchase_date FROM purchases WHERE id = ?");
                    stmt.setInt(1, purchaseId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        purchaseDate = rs.getString("purchase_date");
                    }
                }
            }

            // Update product stock (also updates memory)
            ProductDAO.updateProductStock(productId, -1);

            // Update memory
            Customer customer = MemoryStore.customers.get(customerId);
            if (customer != null) {
                customer.setBalance(customer.getBalance() - price);
            }

            if (purchaseId != -1 && purchaseDate != null) {
                Purchase purchase = new Purchase(purchaseId, customerId, productName, price, purchaseDate);
                MemoryStore.addPurchase(purchase);
            }

            return PurchaseResult.SUCCESS;

        } catch (SQLException e) {
            return PurchaseResult.FAILURE;
        }
    }

    public static ResultSet getDepositsForCustomer(int customerId) throws SQLException {
        String sql = "SELECT total_deposit, deposit_date FROM deposits WHERE customer_id = ? ORDER BY deposit_date DESC";
        PreparedStatement stmt = Database.getConnection().prepareStatement(sql);
        stmt.setInt(1, customerId);
        return stmt.executeQuery();
    }

    public static ResultSet getPurchasesForCustomer(int customerId) throws SQLException {
        String sql = "SELECT product_name, product_price, purchase_date FROM purchases WHERE customer_id = ? ORDER BY purchase_date DESC";
        PreparedStatement stmt = Database.getConnection().prepareStatement(sql);
        stmt.setInt(1, customerId);
        return stmt.executeQuery();
    }
}
