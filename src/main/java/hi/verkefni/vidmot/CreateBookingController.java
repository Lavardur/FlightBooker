package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CreateBookingController {
    private BookingController bookingController;
    private FlightController flightController;
    private Customer customer;
    private boolean bookingCreated = false;
    
    @FXML
    private ComboBox<String> originComboBox;
    
    @FXML
    private ComboBox<String> destinationComboBox;
    
    @FXML
    private DatePicker departureDatePicker;
    
    @FXML
    private Button searchFlightsButton;
    
    @FXML
    private ListView<Flight> flightsListView;
    
    @FXML
    private ListView<Seat> seatsListView;
    
    @FXML
    private Button bookButton;
    
    @FXML
    private Button cancelButton;
    
    public CreateBookingController(BookingController bookingController, 
                                 FlightController flightController, 
                                 Customer customer) {
        this.bookingController = bookingController;
        this.flightController = flightController;
        this.customer = customer;
    }
    
    @FXML
    public void initialize() {
        // Populate origin and destination combo boxes
        List<Flight> allFlights = flightController.getAllFlights();
        List<String> origins = allFlights.stream()
            .map(Flight::getOrigin)
            .distinct()
            .collect(Collectors.toList());
        
        List<String> destinations = allFlights.stream()
            .map(Flight::getDestination)
            .distinct()
            .collect(Collectors.toList());
        
        originComboBox.setItems(FXCollections.observableArrayList(origins));
        destinationComboBox.setItems(FXCollections.observableArrayList(destinations));
        
        // Set default date to today
        departureDatePicker.setValue(LocalDate.now());
        
        // Setup flight list view
        setupFlightsListView();
        
        // Setup seats list view
        setupSeatsListView();
        
        // Add selection listeners
        flightsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    updateAvailableSeats(newSelection);
                } else {
                    seatsListView.getItems().clear();
                }
                updateBookButtonState();
            });
        
        seatsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> updateBookButtonState());
        
        // Initial button states
        updateBookButtonState();
    }
    
    @FXML
    private void handleSearchFlights() {
        String origin = originComboBox.getValue();
        String destination = destinationComboBox.getValue();
        LocalDate departureDate = departureDatePicker.getValue();
        
        if (origin == null || destination == null || departureDate == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", 
                    "Please select origin, destination, and departure date");
            return;
        }
        
        // Convert LocalDate to LocalDateTime (start of day)
        LocalDateTime departureDateTime = departureDate.atStartOfDay();
        
        // Search flights
        List<Flight> flights = flightController.searchFlights(origin, destination, departureDateTime);
        
        if (flights.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Flights", 
                    "No flights found matching your criteria");
        }
        
        flightsListView.setItems(FXCollections.observableArrayList(flights));
        seatsListView.getItems().clear();
        updateBookButtonState();
    }
    
    private void setupFlightsListView() {
        flightsListView.setCellFactory(param -> new ListCell<Flight>() {
            @Override
            protected void updateItem(Flight flight, boolean empty) {
                super.updateItem(flight, empty);
                
                if (empty || flight == null) {
                    setText(null);
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    setText(String.format("%s: %s to %s - Depart: %s - Arrive: %s", 
                                      flight.getFlightNumber(),
                                      flight.getOrigin(),
                                      flight.getDestination(),
                                      flight.getDepartureTime().format(formatter),
                                      flight.getArrivalTime().format(formatter)));
                }
            }
        });
    }
    
    private void setupSeatsListView() {
        seatsListView.setCellFactory(param -> new ListCell<Seat>() {
            @Override
            protected void updateItem(Seat seat, boolean empty) {
                super.updateItem(seat, empty);
                
                if (empty || seat == null) {
                    setText(null);
                } else {
                    setText("Seat " + seat.getSeatNumber());
                }
            }
        });
    }
    
    private void updateAvailableSeats(Flight flight) {
        List<Seat> availableSeats = bookingController.getAvailableSeats(flight.getFlightNumber());
        seatsListView.setItems(FXCollections.observableArrayList(availableSeats));
    }
    
    @FXML
    private void handleBookButton() {
        Flight selectedFlight = flightsListView.getSelectionModel().getSelectedItem();
        Seat selectedSeat = seatsListView.getSelectionModel().getSelectedItem();
        
        if (selectedFlight == null || selectedSeat == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", 
                    "Please select a flight and a seat");
            return;
        }
        
        try {
            Booking booking = bookingController.createBooking(customer, selectedFlight, selectedSeat);
            bookingCreated = true;
            
            showAlert(Alert.AlertType.INFORMATION, "Booking Confirmed", 
                    "Booking created successfully!\nBooking ID: " + booking.getBookingId());
            
            // Close the window
            Stage stage = (Stage) bookButton.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Booking Error", 
                    "Error creating booking: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    private void updateBookButtonState() {
        boolean flightSelected = flightsListView.getSelectionModel().getSelectedItem() != null;
        boolean seatSelected = seatsListView.getSelectionModel().getSelectedItem() != null;
        
        bookButton.setDisable(!(flightSelected && seatSelected));
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public boolean isBookingCreated() {
        return bookingCreated;
    }
}