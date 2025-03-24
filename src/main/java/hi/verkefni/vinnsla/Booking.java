package hi.verkefni.vinnsla;

import java.time.LocalDateTime;

public class Booking {
    private String bookingId;
    private LocalDateTime bookingDate;
    private String status; // CONFIRMED, CANCELLED, etc.
    private String customerId;
    private String flightNumber;
    private String seatNumber;
    
    public Booking(String bookingId, LocalDateTime bookingDate, String status, 
                  String customerId, String flightNumber, String seatNumber) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.status = status;
        this.customerId = customerId;
        this.flightNumber = flightNumber;
        this.seatNumber = seatNumber;
    }
    
    // Simple constructor for when creating a new booking
    public Booking(String bookingId, String customerId, String flightNumber, String seatNumber) {
        this.bookingId = bookingId;
        this.bookingDate = LocalDateTime.now();
        this.status = "CONFIRMED";
        this.customerId = customerId;
        this.flightNumber = flightNumber;
        this.seatNumber = seatNumber;
    }
    
    // Getters and setters
    public String getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}