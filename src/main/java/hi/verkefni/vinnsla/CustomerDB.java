package hi.verkefni.vinnsla;

import java.sql.*;

public class CustomerDB {
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:flightbooker.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    // Create customers table if it doesn't exist
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Customer (\n"
                + "	customerId TEXT PRIMARY KEY,\n"
                + "	name TEXT NOT NULL,\n"
                + "	email TEXT NOT NULL,\n"
                + "	phoneNumber TEXT\n"
                + ");";
        
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public Customer selectById(String customerId) {
        String sql = "SELECT customerId, name, email, phoneNumber FROM Customer WHERE customerId = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Customer(
                    rs.getString("customerId"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phoneNumber")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public void insert(Customer customer) {
        String sql = "INSERT INTO Customer(customerId, name, email, phoneNumber) VALUES(?,?,?,?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhoneNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void update(Customer customer) {
        String sql = "UPDATE Customer SET name = ?, email = ?, phoneNumber = ? WHERE customerId = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, customer.getCustomerId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete(String customerId) {
        String sql = "DELETE FROM Customer WHERE customerId = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
