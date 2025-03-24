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
        // Delete existing database file if it exists
        File dbFile = new File("flightbooker.db");
        if (dbFile.exists()) {
            System.out.println("Deleting existing database file...");
            dbFile.delete();
        }
        
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
                line = line.trim();
                
                // Skip comment lines and empty lines
                if (line.startsWith("//") || line.startsWith("--") || line.startsWith("/*") || line.isEmpty()) {
                    continue;
                }
                
                sql.append(line).append(" ");
                
                // Execute when we reach the end of a statement
                if (line.endsWith(";")) {
                    String sqlStatement = sql.toString().trim();
                    System.out.println("Executing SQL: " + sqlStatement);
                    executeSQL(sqlStatement);
                    sql.setLength(0);
                }
            }
        }
    }
    
    private static void executeSQL(String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("SQL Error executing: " + sql);
            throw e;
        }
    }
    
    public static void main(String[] args) {
        initialize();
    }
}