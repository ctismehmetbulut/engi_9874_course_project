package dao;

import java.sql.*;
import model.Product;

public class ProductDAO {

    public static void updateProductStock(int productId, int quantity) {
        try {
            String checkSql = "SELECT stock, name, price FROM products WHERE id = ?";
            try (PreparedStatement checkStmt = Database.getConnection().prepareStatement(checkSql)) {
                checkStmt.setInt(1, productId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int currentStock = rs.getInt("stock");
                    int newStock = currentStock + quantity;

                    if (newStock > 0) {
                        // Update stock
                        String updateSql = "UPDATE products SET stock = ? WHERE id = ?";
                        try (PreparedStatement updateStmt = Database.getConnection().prepareStatement(updateSql)) {
                            updateStmt.setInt(1, newStock);
                            updateStmt.setInt(2, productId);
                            updateStmt.executeUpdate();
                        }

                        // Update in-memory
                        Product product = MemoryStore.products.get(productId);
                        if (product != null) {
                            product.setStock(newStock);
                        } else {
                            // fallback if memory state is out of sync
                            Product updatedProduct = new Product(productId, rs.getString("name"), rs.getDouble("price"),
                                    newStock);
                            MemoryStore.products.put(productId, updatedProduct);
                        }

                    } else {
                        // Delete product
                        String deleteSql = "DELETE FROM products WHERE id = ?";
                        try (PreparedStatement deleteStmt = Database.getConnection().prepareStatement(deleteSql)) {
                            deleteStmt.setInt(1, productId);
                            deleteStmt.executeUpdate();
                        }

                        // Remove from memory
                        MemoryStore.products.remove(productId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update or delete product", e);
        }
    }

    public static void insertProduct(String name, double price, int stock) {
        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int productId = generatedKeys.getInt(1);
                    Product product = new Product(productId, name, price, stock);
                    MemoryStore.products.put(productId, product);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert product", e);
        }
    }

    public static ResultSet getAllProducts() throws SQLException {
        String sql = "SELECT id, name, price, stock FROM products";
        return Database.getConnection().createStatement().executeQuery(sql);
    }

    public static int getProductIdByName(String productName) {
        String sql = "SELECT id FROM products WHERE name = ?";
        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, productName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get product ID by name", e);
        }
        return -1;
    }

    public static int getProductStock(int productId) {
        String sql = "SELECT stock FROM products WHERE id = ?";
        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get product stock", e);
        }
        return 0;
    }
}
