package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BookingController {
    private BookingDB bookingDB;
    private FlightController flightController;
    private CustomerController customerController;
    
    public BookingController(BookingDB bookingDB, FlightController flightController, CustomerController customerController) {
        this.bookingDB = bookingDB;
        this.flightController = flightController;
        this.customerController = customerController;
    }
    
    /**
     * Creates a new booking for a customer on a flight with a single specified seat
     * 
     * @param customer The customer making the booking
     * @param flight The flight to book
     * @param seat The seat to book
     * @return The created booking
     */
    public Booking createBooking(Customer customer, Flight flight, Seat seat) {
        // Validate customer
        if (customer == null || customerController.getCustomer(customer.getCustomerId()) == null) {
            throw new IllegalArgumentException("Invalid customer");
        }
        
        // Validate flight
        if (flight == null || flightController.getFlightByNumber(flight.getFlightNumber()) == null) {
            throw new IllegalArgumentException("Invalid flight");
        }
        
        // Validate seat
        if (seat == null) {
            throw new IllegalArgumentException("No seat selected");
        }
        
        // Check if seat is available (not booked)
        if (seat.isBooked()) {
            throw new IllegalArgumentException("Seat " + seat.getSeatNumber() + " is already booked");
        }
        
        // Generate a unique booking ID
        String bookingId = "B" + UUID.randomUUID().toString().substring(0, 6);
        
        // Create the booking with the seat
        Booking booking = new Booking(
            bookingId,
            LocalDateTime.now(),
            "CONFIRMED",
            customer.getCustomerId(),
            seat.getFlightNumber(), // Make sure we're using the flight number from the seat!
            seat.getSeatNumber()
        );
        
        // For debugging
        System.out.println("Creating booking with ID: " + bookingId);
        System.out.println("Flight number: " + seat.getFlightNumber());
        System.out.println("Seat number: " + seat.getSeatNumber());
        
        // Insert into database
        bookingDB.insert(booking);
        
        return booking;
    }
    
    /**
     * Cancels an existing booking
     * 
     * @param bookingId The booking ID to cancel
     * @return true if cancelled successfully, false otherwise
     */
    public boolean cancelBooking(String bookingId) {
        Booking booking = bookingDB.selectById(bookingId);
        
        if (booking == null) {
            return false;
        }
        
        // Update status to CANCELLED
        booking.setStatus("CANCELLED");
        bookingDB.update(booking);
        
        return true;
    }
    
    /**
     * Updates an existing booking with a new seat
     * 
     * @param bookingId The booking ID to update
     * @param newSeat New seat to assign to this booking
     * @return The updated booking
     */
    public Booking updateBooking(String bookingId, Seat newSeat) {
        Booking booking = bookingDB.selectById(bookingId);
        
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        
        if (newSeat == null) {
            throw new IllegalArgumentException("No seat selected");
        }
        
        // Check if seat is available
        if (newSeat.isBooked()) {
            throw new IllegalArgumentException("Seat " + newSeat.getSeatNumber() + " is already booked");
        }
        
        // Update booking with new seat
        booking.setSeatNumber(newSeat.getSeatNumber());
        bookingDB.update(booking);
        
        return booking;
    }
    
    /**
     * Retrieves booking details
     * 
     * @param bookingId The booking ID to retrieve
     * @return The booking or null if not found
     */
    public Booking viewBooking(String bookingId) {
        return bookingDB.selectById(bookingId);
    }
    
    /**
     * Gets all bookings for a customer
     * 
     * @param customerId The customer ID
     * @return List of bookings for the customer
     */
    public List<Booking> getBookingsByCustomer(String customerId) {
        return bookingDB.selectByCustomerId(customerId);
    }
    
    /**
     * Gets available seats for a flight
     * 
     * @param flightNumber The flight number
     * @return List of available seats
     */
    public List<Seat> getAvailableSeats(String flightNumber) {
        return bookingDB.getAvailableSeats(flightNumber);
    }
    
    /**
     * Gets the seat for a specific booking
     * 
     * @param bookingId The booking ID
     * @return The seat, or null if booking not found
     */
    public Seat getBookedSeat(String bookingId) {
        Booking booking = bookingDB.selectById(bookingId);
        if (booking == null) {
            return null;
        }
        
        return new Seat(booking.getSeatNumber(), true, booking.getFlightNumber());
    }
}
