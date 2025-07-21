package dao;
import java.io.*;
import java.sql.*;

public class Database {
    private static Connection connection;
    // private static final Connection connection = DatabaseConnector.connect();
    public static Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            connect();
        }
        return connection;
    }
    
    private static boolean isConnectionClosed() {
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            return true; // Assume closed if we can't check
        }
    }

    public static Connection connect() {
        String dbFilePath = "data.db";
        try {
            // Close existing connection if it exists
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            
            String url = "jdbc:sqlite:" + dbFilePath;
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database: " + new File(dbFilePath).getAbsolutePath());
            ensureSchema("schema.sql");
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
        return connection;
    }

    private static void ensureSchema(String filePath) {
        try {
            if (!tableExists("campaigns") || !tableExists("products") || !tableExists("customers") || !tableExists("purchases") || !tableExists("deposits")) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                }
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(sb.toString());
                }
                System.out.println("Schema created from: " + filePath);
            } else {
                System.out.println("Schema already exists, skipping SQL import.");
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to load schema from file", e);
        }
    }

    private static boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
                connection = null;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close connection", e);
            }
        }
    }
}
