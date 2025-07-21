package dao;

import java.sql.*;
import model.Customer;

public class CustomerDAO {
    public static boolean signupCustomer(String name, String pin) {
        String checkSql = "SELECT id FROM customers WHERE name = ?";
        String insertSql = "INSERT INTO customers (name, password) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            
            // Check if username already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, name);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    return false; // Username already exists
                }
            }

            // Insert new customer
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, pin);
                insertStmt.executeUpdate();

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int customerId = generatedKeys.getInt(1);
                        Customer customer = new Customer(customerId, name, pin, 0.0); // Assuming balance = 0.0 on signup
                        MemoryStore.customers.put(customerId, customer);
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("SQL Error during customer signup: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to sign up customer", e);
        }
    }

    public static Integer authenticateCustomer(String name, String pin) {
        String sql = "SELECT id FROM customers WHERE name = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, pin);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Return customer ID
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to authenticate customer", e);
        }
    }

    public static double getCustomerBalance(int customerId) {
        String sql = "SELECT balance FROM customers WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get customer balance", e);
        }
        return 0;
    }
}
