package hi.verkefni.vinnsla;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightDB {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private Connection connect() {
        String url = "jdbc:sqlite:flightbooker.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    public Flight selectByFlightNumber(String flightNumber) {
        String sql = "SELECT flightNumber, origin, destination, departureTime, arrivalTime " +
                     "FROM Flight WHERE flightNumber = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flightNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Flight(
                    rs.getString("flightNumber"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    LocalDateTime.parse(rs.getString("departureTime"), formatter),
                    LocalDateTime.parse(rs.getString("arrivalTime"), formatter)
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    /**
     * Flexible flight search with optional parameters
     * 
     * @param origin Optional origin airport code
     * @param destination Optional destination airport code
     * @param date Optional departure date
     * @return List of matching flights
     */
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        // Start building the SQL query with a base
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT flightNumber, origin, destination, departureTime, arrivalTime FROM Flight WHERE 1=1");
        
        // Keep track of parameters
        List<Object> params = new ArrayList<>();
        
        // Add conditions based on provided parameters
        if (origin != null && !origin.isEmpty()) {
            sqlBuilder.append(" AND origin = ?");
            params.add(origin);
        }
        
        if (destination != null && !destination.isEmpty()) {
            sqlBuilder.append(" AND destination = ?");
            params.add(destination);
        }
        
        if (date != null) {
            sqlBuilder.append(" AND date(departureTime) = ?");
            params.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        // Add order by clause
        sqlBuilder.append(" ORDER BY departureTime");
        
        String sql = sqlBuilder.toString();
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                flights.add(new Flight(
                    rs.getString("flightNumber"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    LocalDateTime.parse(rs.getString("departureTime"), formatter),
                    LocalDateTime.parse(rs.getString("arrivalTime"), formatter)
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return flights;
    }
    
    /**
     * Search flights by origin
     * 
     * @param origin The origin airport code
     * @return List of matching flights
     */
    public List<Flight> searchByOrigin(String origin) {
        return searchFlights(origin, null, null);
    }
    
    /**
     * Search flights by destination
     * 
     * @param destination The destination airport code
     * @return List of matching flights
     */
    public List<Flight> searchByDestination(String destination) {
        return searchFlights(null, destination, null);
    }
    
    /**
     * Search flights by departure date
     * 
     * @param date The departure date
     * @return List of matching flights
     */
    public List<Flight> searchByDate(LocalDateTime date) {
        return searchFlights(null, null, date);
    }
    
    public List<Flight> getAllFlights() {
        String sql = "SELECT flightNumber, origin, destination, departureTime, arrivalTime FROM Flight";
        
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                flights.add(new Flight(
                    rs.getString("flightNumber"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    LocalDateTime.parse(rs.getString("departureTime"), formatter),
                    LocalDateTime.parse(rs.getString("arrivalTime"), formatter)
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return flights;
    }
    
    /**
     * Get all distinct origin airports
     * 
     * @return List of origin airport codes
     */
    public List<String> getAllOrigins() {
        String sql = "SELECT DISTINCT origin FROM Flight ORDER BY origin";
        List<String> origins = new ArrayList<>();
        
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                origins.add(rs.getString("origin"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return origins;
    }
    
    /**
     * Get all distinct destination airports
     * 
     * @return List of destination airport codes
     */
    public List<String> getAllDestinations() {
        String sql = "SELECT DISTINCT destination FROM Flight ORDER BY destination";
        List<String> destinations = new ArrayList<>();
        
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                destinations.add(rs.getString("destination"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return destinations;
    }
}
