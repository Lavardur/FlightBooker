package hi.verkefni.vinnsla;

public class Seat {
    private String seatNumber;
    private boolean booked;
    private String flightNumber;
    
    public Seat(String seatNumber, boolean booked, String flightNumber) {
        this.seatNumber = seatNumber;
        this.booked = booked;
        this.flightNumber = flightNumber;
    }
    
    // Getters and setters
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
    public boolean isBooked() {
        return booked;
    }
    
    public void setBooked(boolean booked) {
        this.booked = booked;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
}