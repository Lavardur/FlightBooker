package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class FlightBookerApp {
    private CustomerController customerController;
    private FlightController flightController;
    private BookingController bookingController;
    private Scanner scanner;
    
    public FlightBookerApp() {
        // Initialize database with schema and test data
        DatabaseInitializer.initialize();
        
        // Initialize controllers
        CustomerDB customerDB = new CustomerDB();
        FlightDB flightDB = new FlightDB();
        BookingDB bookingDB = new BookingDB();
        
        customerController = new CustomerController(customerDB);
        flightController = new FlightController(flightDB);
        bookingController = new BookingController(bookingDB, flightController, customerController);
        
        scanner = new Scanner(System.in);
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== Flight Booker System =====");
            System.out.println("1. Search Flights");
            System.out.println("2. View Customer Details");
            System.out.println("3. Create Booking");
            System.out.println("4. View Booking");
            System.out.println("5. Cancel Booking");
            System.out.println("6. Update Booking (Change Seat)");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    searchFlights();
                    break;
                case 2:
                    viewCustomerDetails();
                    break;
                case 3:
                    createBooking();
                    break;
                case 4:
                    viewBooking();
                    break;
                case 5:
                    cancelBooking();
                    break;
                case 6:
                    updateBooking();
                    break;
                case 7:
                    running = false;
                    System.out.println("Thank you for using Flight Booker!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private void searchFlights() {
        System.out.println("\n----- Flight Search -----");
        System.out.print("Enter origin (e.g., KEF): ");
        String origin = scanner.nextLine().toUpperCase();
        
        System.out.print("Enter destination (e.g., JFK): ");
        String destination = scanner.nextLine().toUpperCase();
        
        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        LocalDateTime date = LocalDateTime.parse(dateStr + " 00:00:00", 
                                               DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        List<Flight> flights = flightController.searchFlights(origin, destination, date);
        
        if (flights.isEmpty()) {
            System.out.println("No flights found for the specified criteria.");
        } else {
            System.out.println("\nAvailable Flights:");
            System.out.printf("%-10s %-5s %-5s %-20s %-20s\n", 
                             "Flight #", "From", "To", "Departure", "Arrival");
            System.out.println("------------------------------------------------------------");
            
            DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Flight flight : flights) {
                System.out.printf("%-10s %-5s %-5s %-20s %-20s\n", 
                                 flight.getFlightNumber(),
                                 flight.getOrigin(),
                                 flight.getDestination(),
                                 flight.getDepartureTime().format(displayFormat),
                                 flight.getArrivalTime().format(displayFormat));
            }
        }
    }
    
    private void viewCustomerDetails() {
        System.out.println("\n----- Customer Details -----");
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();
        
        Customer customer = customerController.getCustomer(customerId);
        
        if (customer == null) {
            System.out.println("Customer not found.");
        } else {
            System.out.println("\nCustomer Information:");
            System.out.println("ID: " + customer.getCustomerId());
            System.out.println("Name: " + customer.getName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Phone: " + customer.getPhoneNumber());
            
            // Show customer's bookings
            List<Booking> bookings = bookingController.getBookingsByCustomer(customerId);
            if (!bookings.isEmpty()) {
                System.out.println("\nCustomer Bookings:");
                System.out.printf("%-10s %-10s %-10s %-10s\n", 
                                 "Booking ID", "Flight", "Seat", "Status");
                System.out.println("------------------------------------------");
                
                for (Booking booking : bookings) {
                    System.out.printf("%-10s %-10s %-10s %-10s\n", 
                                     booking.getBookingId(),
                                     booking.getFlightNumber(),
                                     booking.getSeatNumber(),
                                     booking.getStatus());
                }
            } else {
                System.out.println("\nNo bookings found for this customer.");
            }
        }
    }
    
    private void createBooking() {
        System.out.println("\n----- Create Booking -----");
        
        // Get customer
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();
        Customer customer = customerController.getCustomer(customerId);
        
        if (customer == null) {
            System.out.println("Customer not found. Please register first.");
            return;
        }
        
        // Get flight
        System.out.print("Enter Flight Number: ");
        String flightNumber = scanner.nextLine();
        Flight flight = flightController.getFlightByNumber(flightNumber);
        
        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }
        
        // Show available seats
        List<Seat> availableSeats = bookingController.getAvailableSeats(flightNumber);
        
        if (availableSeats.isEmpty()) {
            System.out.println("No seats available for this flight.");
            return;
        }
        
        System.out.println("\nAvailable Seats:");
        for (int i = 0; i < availableSeats.size(); i++) {
            System.out.print(availableSeats.get(i).getSeatNumber());
            
            // Print 10 seats per line
            if ((i + 1) % 10 == 0) {
                System.out.println();
            } else {
                System.out.print("\t");
            }
        }
        System.out.println();
        
        // Select seat
        System.out.print("Enter Seat Number: ");
        String seatNumber = scanner.nextLine().toUpperCase();
        
        // Find the selected seat in the list
        Seat selectedSeat = null;
        for (Seat seat : availableSeats) {
            if (seat.getSeatNumber().equals(seatNumber)) {
                selectedSeat = seat;
                break;
            }
        }
        
        if (selectedSeat == null) {
            System.out.println("Invalid seat selection or seat is already booked.");
            return;
        }
        
        // Create booking
        try {
            Booking booking = bookingController.createBooking(customer, flight, selectedSeat);
            System.out.println("Booking successful!");
            System.out.println("Booking ID: " + booking.getBookingId());
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating booking: " + e.getMessage());
        }
    }
    
    private void viewBooking() {
        System.out.println("\n----- View Booking -----");
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookingController.viewBooking(bookingId);
        
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        System.out.println("\nBooking Details:");
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Date: " + booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Status: " + booking.getStatus());
        System.out.println("Customer ID: " + booking.getCustomerId());
        System.out.println("Flight: " + booking.getFlightNumber());
        System.out.println("Seat: " + booking.getSeatNumber());
        
        // Get customer details
        Customer customer = customerController.getCustomer(booking.getCustomerId());
        if (customer != null) {
            System.out.println("\nCustomer Information:");
            System.out.println("Name: " + customer.getName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Phone: " + customer.getPhoneNumber());
        }
        
        // Get flight details
        Flight flight = flightController.getFlightByNumber(booking.getFlightNumber());
        if (flight != null) {
            System.out.println("\nFlight Information:");
            System.out.println("From: " + flight.getOrigin());
            System.out.println("To: " + flight.getDestination());
            System.out.println("Departure: " + flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            System.out.println("Arrival: " + flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
    }
    
    private void cancelBooking() {
        System.out.println("\n----- Cancel Booking -----");
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();
        
        boolean success = bookingController.cancelBooking(bookingId);
        
        if (success) {
            System.out.println("Booking cancelled successfully.");
        } else {
            System.out.println("Failed to cancel booking. Booking not found.");
        }
    }
    
    private void updateBooking() {
        System.out.println("\n----- Update Booking (Change Seat) -----");
        System.out.print("Enter Booking ID: ");
        String bookingId = scanner.nextLine();
        
        Booking booking = bookingController.viewBooking(bookingId);
        
        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        if (!"CONFIRMED".equals(booking.getStatus())) {
            System.out.println("Cannot update a booking that is not in CONFIRMED status.");
            return;
        }
        
        // Show available seats
        List<Seat> availableSeats = bookingController.getAvailableSeats(booking.getFlightNumber());
        
        if (availableSeats.isEmpty()) {
            System.out.println("No other seats available for this flight.");
            return;
        }
        
        System.out.println("\nCurrent Seat: " + booking.getSeatNumber());
        System.out.println("Available Seats:");
        for (int i = 0; i < availableSeats.size(); i++) {
            System.out.print(availableSeats.get(i).getSeatNumber());
            
            // Print 10 seats per line
            if ((i + 1) % 10 == 0) {
                System.out.println();
            } else {
                System.out.print("\t");
            }
        }
        System.out.println();
        
        // Select new seat
        System.out.print("Enter New Seat Number: ");
        String seatNumber = scanner.nextLine().toUpperCase();
        
        // Find the selected seat in the list
        Seat selectedSeat = null;
        for (Seat seat : availableSeats) {
            if (seat.getSeatNumber().equals(seatNumber)) {
                selectedSeat = seat;
                break;
            }
        }
        
        if (selectedSeat == null) {
            System.out.println("Invalid seat selection or seat is already booked.");
            return;
        }
        
        // Update booking
        try {
            bookingController.updateBooking(bookingId, selectedSeat);
            System.out.println("Booking updated successfully. New seat: " + seatNumber);
        } catch (IllegalArgumentException e) {
            System.out.println("Error updating booking: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        FlightBookerApp app = new FlightBookerApp();
        app.start();
    }
}