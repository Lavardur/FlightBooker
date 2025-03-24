package hi.verkefni.vinnsla;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingDB {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    
    // Select a booking by ID
    public Booking selectById(String bookingId) {
        String sql = "SELECT bookingId, bookingDate, status, customerId, flightNumber, seatNumber " +
                     "FROM Booking WHERE bookingId = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Booking(
                    rs.getString("bookingId"),
                    LocalDateTime.parse(rs.getString("bookingDate"), formatter),
                    rs.getString("status"),
                    rs.getString("customerId"),
                    rs.getString("flightNumber"),
                    rs.getString("seatNumber")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    // Get bookings by customer ID
    public List<Booking> selectByCustomerId(String customerId) {
        String sql = "SELECT bookingId, bookingDate, status, customerId, flightNumber, seatNumber " +
                     "FROM Booking WHERE customerId = ?";
        
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getString("bookingId"),
                    LocalDateTime.parse(rs.getString("bookingDate"), formatter),
                    rs.getString("status"),
                    rs.getString("customerId"),
                    rs.getString("flightNumber"),
                    rs.getString("seatNumber")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return bookings;
    }
    
    // Insert a new booking
    public void insert(Booking booking) {
        String sql = "INSERT INTO Booking(bookingId, bookingDate, status, customerId, flightNumber, seatNumber) " +
                     "VALUES(?,?,?,?,?,?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            // Set transaction to handle multi-table operations atomically
            conn.setAutoCommit(false);
            
            pstmt.setString(1, booking.getBookingId());
            pstmt.setString(2, booking.getBookingDate().format(formatter));
            pstmt.setString(3, booking.getStatus());
            pstmt.setString(4, booking.getCustomerId());
            pstmt.setString(5, booking.getFlightNumber());
            pstmt.setString(6, booking.getSeatNumber());
            pstmt.executeUpdate();
            
            // Update seat status in Seat table
            updateSeatStatus(conn, booking.getFlightNumber(), booking.getSeatNumber(), true);
            
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // Update an existing booking
    public void update(Booking booking) {
        // First get the old booking to know which seat to free
        Booking oldBooking = selectById(booking.getBookingId());
        if (oldBooking == null) {
            return;
        }
        
        String sql = "UPDATE Booking SET bookingDate = ?, status = ?, customerId = ?, " +
                     "flightNumber = ?, seatNumber = ? WHERE bookingId = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set transaction to handle multi-table update atomically
            conn.setAutoCommit(false);
            
            // If the seat has changed, free the old one and book the new one
            if (!oldBooking.getSeatNumber().equals(booking.getSeatNumber()) || 
                !oldBooking.getFlightNumber().equals(booking.getFlightNumber())) {
                // Free the old seat
                updateSeatStatus(conn, oldBooking.getFlightNumber(), oldBooking.getSeatNumber(), false);
                
                // Book the new seat
                updateSeatStatus(conn, booking.getFlightNumber(), booking.getSeatNumber(), true);
            }
            
            pstmt.setString(1, booking.getBookingDate().format(formatter));
            pstmt.setString(2, booking.getStatus());
            pstmt.setString(3, booking.getCustomerId());
            pstmt.setString(4, booking.getFlightNumber());
            pstmt.setString(5, booking.getSeatNumber());
            pstmt.setString(6, booking.getBookingId());
            pstmt.executeUpdate();
            
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // Delete a booking
    public void delete(String bookingId) {
        // First, get the booking to know which seat to free
        Booking booking = selectById(bookingId);
        if (booking == null) {
            return;
        }
        
        String sql = "DELETE FROM Booking WHERE bookingId = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set transaction to handle multi-table operations atomically
            conn.setAutoCommit(false);
            
            // Free the seat
            updateSeatStatus(conn, booking.getFlightNumber(), booking.getSeatNumber(), false);
            
            // Delete the booking
            pstmt.setString(1, bookingId);
            pstmt.executeUpdate();
            
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // Helper method to update seat status
    private void updateSeatStatus(Connection conn, String flightNumber, String seatNumber, boolean status) 
            throws SQLException {
        String sql = "UPDATE Seat SET seatStatus = ? WHERE flightNumber = ? AND seatNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, status);
            pstmt.setString(2, flightNumber);
            pstmt.setString(3, seatNumber);
            pstmt.executeUpdate();
        }
    }
    
    // Get available seats for a flight
    public List<Seat> getAvailableSeats(String flightNumber) {
        String sql = "SELECT seatNumber, seatStatus FROM Seat WHERE flightNumber = ? AND seatStatus = 0";
        List<Seat> seats = new ArrayList<>();
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flightNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                seats.add(new Seat(
                    rs.getString("seatNumber"),
                    false,  // Not booked (available)
                    flightNumber
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return seats;
    }
}
