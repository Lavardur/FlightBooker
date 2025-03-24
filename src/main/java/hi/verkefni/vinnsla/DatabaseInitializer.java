package hi.verkefni.vinnsla;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:sqlite:flightbooker.db";
    
    public static void initialize() {
        createTables();
        loadTestData();
    }
    
    private static void createTables() {
        try {
            executeSqlFile("db/schema.sql");
            System.out.println("Database schema created successfully");
        } catch (IOException | SQLException e) {
            System.err.println("Error creating database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void loadTestData() {
        try {
            executeSqlFile("db/insert.sql");
            System.out.println("Test data loaded successfully");
        } catch (IOException | SQLException e) {
            System.err.println("Error loading test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void executeSqlFile(String filePath) throws IOException, SQLException {
        File file = new File(filePath);
        StringBuilder sql = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comment lines and empty lines
                if (line.trim().startsWith("//") || line.trim().startsWith("/*") || line.trim().isEmpty()) {
                    continue;
                }
                sql.append(line);
                
                // Execute when we reach the end of a statement
                if (line.trim().endsWith(";")) {
                    executeSQL(sql.toString());
                    sql.setLength(0);
                }
            }
        }
    }
    
    private static void executeSQL(String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    public static void main(String[] args) {
        initialize();
    }
}