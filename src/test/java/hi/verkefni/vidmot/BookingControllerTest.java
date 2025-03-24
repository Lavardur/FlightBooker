package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingControllerTest {
    private MockBookingDB mockBookingDB;
    private MockCustomerDB mockCustomerDB;
    private MockFlightDB mockFlightDB;
    private BookingController bookingController;
    private CustomerController customerController;
    private FlightController flightController;
    
    private Customer testCustomer;
    private Flight testFlight;
    
    @BeforeEach
    public void setUp() {
        mockBookingDB = new MockBookingDB();
        mockCustomerDB = new MockCustomerDB();
        mockFlightDB = new MockFlightDB();
        
        customerController = new CustomerController(mockCustomerDB);
        flightController = new FlightController(mockFlightDB);
        bookingController = new BookingController(mockBookingDB, flightController, customerController);
        
        // Create test customer
        testCustomer = new Customer("TEST001", "Test User", "test@example.com", "5551234");
        mockCustomerDB.insert(testCustomer);
        
        // Create test flight - use "FI101" to match what's in MockBookingDB
        testFlight = new Flight("FI101", "KEF", "LHR", 
                              LocalDateTime.of(2024, 12, 1, 10, 0), 
                              LocalDateTime.of(2024, 12, 1, 14, 0));
        mockFlightDB.addFlight(testFlight);
    }
    
    @Test
    public void testCreateBooking() {
        // Get available seats
        List<Seat> availableSeats = mockBookingDB.getAvailableSeats("FI101");
        Seat seatToBook = availableSeats.get(0);
        
        // Create booking
        Booking booking = bookingController.createBooking(testCustomer, testFlight, seatToBook);
        
        // Verify booking was created
        assertEquals(1, mockBookingDB.getInsertCalls(), "Insert method should be called once");
        assertEquals(1, mockBookingDB.getBookingCount(), "One booking should be in the mock database");
        assertEquals("CONFIRMED", booking.getStatus(), "Status should be CONFIRMED");
        assertEquals(testCustomer.getCustomerId(), booking.getCustomerId(), "Customer ID should match");
        assertEquals(testFlight.getFlightNumber(), booking.getFlightNumber(), "Flight number should match");
        assertEquals(seatToBook.getSeatNumber(), booking.getSeatNumber(), "Seat number should match");
    }
    
    @Test
    public void testCancelBooking() {
        // Create a booking first
        List<Seat> seats = mockBookingDB.getAvailableSeats("FI101");
        Booking booking = bookingController.createBooking(testCustomer, testFlight, seats.get(0));
        
        // Cancel the booking
        boolean result = bookingController.cancelBooking(booking.getBookingId());
        
        // Verify cancellation
        assertTrue(result, "Cancellation should succeed");
        assertEquals(1, mockBookingDB.getUpdateCalls(), "Update method should be called once");
        
        // Verify status was updated
        Booking updatedBooking = bookingController.viewBooking(booking.getBookingId());
        assertEquals("CANCELLED", updatedBooking.getStatus(), "Status should be CANCELLED");
    }
    
    @Test
    public void testUpdateBooking() {
        // Create a booking first
        List<Seat> seats = mockBookingDB.getAvailableSeats("FI101");
        Booking booking = bookingController.createBooking(testCustomer, testFlight, seats.get(0));
        
        // Update with different seat
        Seat newSeat = seats.get(1);
        Booking updatedBooking = bookingController.updateBooking(booking.getBookingId(), newSeat);
        
        // Verify update
        assertEquals(1, mockBookingDB.getUpdateCalls(), "Update method should be called once");
        assertEquals(newSeat.getSeatNumber(), updatedBooking.getSeatNumber(), "Seat number should be updated");
    }
    
    @Test
    public void testViewBooking() {
        // Create a booking first
        List<Seat> seats = mockBookingDB.getAvailableSeats("FI101");
        Booking booking = bookingController.createBooking(testCustomer, testFlight, seats.get(0));
        
        // View the booking
        Booking viewedBooking = bookingController.viewBooking(booking.getBookingId());
        
        // Verify
        assertEquals(1, mockBookingDB.getSelectByIdCalls(), "Select method should be called once");
        assertNotNull(viewedBooking, "Booking should be found");
        assertEquals(booking.getBookingId(), viewedBooking.getBookingId(), "Booking ID should match");
        assertEquals(booking.getSeatNumber(), viewedBooking.getSeatNumber(), "Seat number should match");
    }
    
    @Test
    public void testGetBookingsByCustomer() {
        // Create multiple bookings for the same customer
        List<Seat> seats = mockBookingDB.getAvailableSeats("FI101");
        bookingController.createBooking(testCustomer, testFlight, seats.get(0));
        bookingController.createBooking(testCustomer, testFlight, seats.get(1));
        
        // Get bookings for customer
        List<Booking> customerBookings = bookingController.getBookingsByCustomer(testCustomer.getCustomerId());
        
        // Verify
        assertEquals(1, mockBookingDB.getSelectByCustomerIdCalls(), "SelectByCustomerId method should be called once");
        assertEquals(2, customerBookings.size(), "Should have 2 bookings");
    }
    
    @Test
    public void testGetAvailableSeats() {
        // Get initial available seats
        String flightNumber = "FI101";  // Use a specific flight number
        List<Seat> allSeats = mockBookingDB.getAvailableSeats(flightNumber);
        int initialCount = allSeats.size();
        System.out.println("Initial seat count: " + initialCount);
        
        // Book a seat
        Seat seatToBook = allSeats.get(0);
        bookingController.createBooking(testCustomer, testFlight, seatToBook);
        
        // Reset the counter for clean testing
        mockBookingDB.resetGetAvailableSeatsCalls();
        
        // Get available seats after booking
        List<Seat> availableSeats = bookingController.getAvailableSeats(flightNumber);
        System.out.println("Available seat count after booking: " + availableSeats.size());
        
        // Verify
        assertEquals(1, mockBookingDB.getGetAvailableSeatsCalls(), "GetAvailableSeats method should be called once");
        assertEquals(initialCount - 1, availableSeats.size(), "Should have 1 fewer seat available");
    }
    
    @Test
    public void testGetBookedSeat() {
        // Create a booking first
        List<Seat> seats = mockBookingDB.getAvailableSeats("FI101");
        Seat originalSeat = seats.get(0);
        Booking booking = bookingController.createBooking(testCustomer, testFlight, originalSeat);
        
        // Get the booked seat
        Seat bookedSeat = bookingController.getBookedSeat(booking.getBookingId());
        
        // Verify
        assertNotNull(bookedSeat, "Booked seat should be found");
        assertEquals(originalSeat.getSeatNumber(), bookedSeat.getSeatNumber(), "Seat number should match");
        assertTrue(bookedSeat.isBooked(), "Seat should be marked as booked");
    }
}