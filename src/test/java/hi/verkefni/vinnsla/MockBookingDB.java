package hi.verkefni.vinnsla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MockBookingDB extends BookingDB {
    private Map<String, Booking> bookings = new HashMap<>();
    private Map<String, Map<String, Seat>> flightSeats = new HashMap<>();
    
    // Track method calls for verification in tests
    private int selectByIdCalls = 0;
    private int selectByCustomerIdCalls = 0;
    private int insertCalls = 0;
    private int updateCalls = 0;
    private int deleteCalls = 0;
    private int getAvailableSeatsCalls = 0;  // This field needs to be accessed
    
    public MockBookingDB() {
        // Initialize with some test flights and seats
        initializeTestData();
    }
    
    private void initializeTestData() {
        // Add flights
        String[] flightNumbers = {"FI101", "FI102", "FI103"};
        
        // Add seats to each flight
        for (String flightNumber : flightNumbers) {
            Map<String, Seat> seats = new HashMap<>();
            // Add seats A1-A10, B1-B10, etc.
            for (char row = 'A'; row <= 'C'; row++) {
                for (int col = 1; col <= 10; col++) {
                    String seatNumber = row + String.valueOf(col);
                    seats.put(seatNumber, new Seat(seatNumber, false, flightNumber));
                }
            }
            flightSeats.put(flightNumber, seats);
        }
    }
    
    @Override
    public Booking selectById(String bookingId) {
        selectByIdCalls++;
        return bookings.get(bookingId);
    }
    
    @Override
    public List<Booking> selectByCustomerId(String customerId) {
        selectByCustomerIdCalls++;
        return bookings.values().stream()
            .filter(booking -> booking.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    
    @Override
    public void insert(Booking booking) {
        insertCalls++;
        bookings.put(booking.getBookingId(), booking);
        
        // Mark seat as booked
        if (flightSeats.containsKey(booking.getFlightNumber())) {
            Map<String, Seat> seats = flightSeats.get(booking.getFlightNumber());
            Seat seat = seats.get(booking.getSeatNumber());
            if (seat != null) {
                seat.setBooked(true);
                // Log for debugging
                System.out.println("Marking seat " + booking.getSeatNumber() + 
                                 " on flight " + booking.getFlightNumber() + " as booked");
            }
        }
    }
    
    @Override
    public void update(Booking booking) {
        updateCalls++;
        
        if (!bookings.containsKey(booking.getBookingId())) {
            return;
        }
        
        Booking oldBooking = bookings.get(booking.getBookingId());
        
        // If seat has changed, free the old one and book the new one
        if (!oldBooking.getSeatNumber().equals(booking.getSeatNumber()) ||
            !oldBooking.getFlightNumber().equals(booking.getFlightNumber())) {
            
            // Free old seat
            if (flightSeats.containsKey(oldBooking.getFlightNumber())) {
                Seat oldSeat = flightSeats.get(oldBooking.getFlightNumber()).get(oldBooking.getSeatNumber());
                if (oldSeat != null) {
                    oldSeat.setBooked(false);
                }
            }
            
            // Book new seat
            if (flightSeats.containsKey(booking.getFlightNumber())) {
                Seat newSeat = flightSeats.get(booking.getFlightNumber()).get(booking.getSeatNumber());
                if (newSeat != null) {
                    newSeat.setBooked(true);
                }
            }
        }
        
        bookings.put(booking.getBookingId(), booking);
    }
    
    @Override
    public void delete(String bookingId) {
        deleteCalls++;
        
        Booking booking = bookings.get(bookingId);
        if (booking != null) {
            // Free seat
            if (flightSeats.containsKey(booking.getFlightNumber())) {
                Seat seat = flightSeats.get(booking.getFlightNumber()).get(booking.getSeatNumber());
                if (seat != null) {
                    seat.setBooked(false);
                }
            }
            
            bookings.remove(bookingId);
        }
    }
    
    @Override
    public List<Seat> getAvailableSeats(String flightNumber) {
        getAvailableSeatsCalls++;
        
        List<Seat> availableSeats = new ArrayList<>();
        
        if (flightSeats.containsKey(flightNumber)) {
            Map<String, Seat> seats = flightSeats.get(flightNumber);
            for (Seat seat : seats.values()) {
                // Log for debugging
                System.out.println("Seat " + seat.getSeatNumber() + " is booked: " + seat.isBooked());
                if (!seat.isBooked()) {
                    availableSeats.add(new Seat(seat.getSeatNumber(), false, flightNumber));
                }
            }
        }
        
        return availableSeats;
    }
    
    // Methods to help with test verification
    public int getSelectByIdCalls() {
        return selectByIdCalls;
    }
    
    public int getSelectByCustomerIdCalls() {
        return selectByCustomerIdCalls;
    }
    
    public int getInsertCalls() {
        return insertCalls;
    }
    
    public int getUpdateCalls() {
        return updateCalls;
    }
    
    public int getDeleteCalls() {
        return deleteCalls;
    }
    
    public int getGetAvailableSeatsCalls() {
        return getAvailableSeatsCalls;
    }
    
    // Add this method to reset the counter
    public void resetGetAvailableSeatsCalls() {
        this.getAvailableSeatsCalls = 0;
    }
    
    public void reset() {
        bookings.clear();
        
        // Reset seats
        for (Map<String, Seat> seats : flightSeats.values()) {
            for (Seat seat : seats.values()) {
                seat.setBooked(false);
            }
        }
        
        selectByIdCalls = 0;
        selectByCustomerIdCalls = 0;
        insertCalls = 0;
        updateCalls = 0;
        deleteCalls = 0;
        getAvailableSeatsCalls = 0;
    }
    
    public int getBookingCount() {
        return bookings.size();
    }
}