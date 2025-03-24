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
    
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String sql = "SELECT flightNumber, origin, destination, departureTime, arrivalTime " +
                     "FROM Flight WHERE origin = ? AND destination = ? " +
                     "AND date(departureTime) = ?";
        
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, origin);
            pstmt.setString(2, destination);
            pstmt.setString(3, formattedDate);
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
}
