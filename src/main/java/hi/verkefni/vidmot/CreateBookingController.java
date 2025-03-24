package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CreateBookingController {
    private BookingController bookingController;
    private FlightController flightController;
    private Customer customer;
    private boolean bookingCreated = false;
    
    @FXML
    private Label customerNameLabel;
    
    @FXML
    private Label selectedFlightLabel;
    
    @FXML
    private Button selectFlightButton;
    
    @FXML
    private ListView<Seat> seatsListView;
    
    @FXML
    private Button bookButton;
    
    @FXML
    private Button cancelButton;
    
    // The selected flight from the flight search view
    private Flight selectedFlight;
    
    public CreateBookingController(BookingController bookingController, 
                                 FlightController flightController, 
                                 Customer customer) {
        this.bookingController = bookingController;
        this.flightController = flightController;
        this.customer = customer;
    }
    
    @FXML
    public void initialize() {
        // Display customer information
        customerNameLabel.setText(customer.getName());
        
        // Initialize as no flight selected yet
        selectedFlightLabel.setText("No flight selected");
        
        // Setup seats list view
        setupSeatsListView();
        
        // Initial button states
        updateButtonStates();
    }
    
    @FXML
    private void handleSelectFlight() {
        try {
            // Open the flight search dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/flight-search-view.fxml"));
            FlightSearchController controller = new FlightSearchController(flightController);
            loader.setController(controller);
            
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Search Flights");
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with parent window
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Get the selected flight
            Flight flight = controller.getSelectedFlight();
            
            if (flight != null) {
                // Update the selected flight
                selectedFlight = flight;
                
                // Update the UI
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                selectedFlightLabel.setText(String.format("%s: %s to %s - Depart: %s", 
                    flight.getFlightNumber(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartureTime().format(formatter)));
                
                // Load available seats for this flight
                updateAvailableSeats();
                
                // Update button states
                updateButtonStates();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open flight search dialog");
        }
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
        
        // Add selection listener to update button states
        seatsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> updateButtonStates());
    }
    
    private void updateAvailableSeats() {
        if (selectedFlight != null) {
            List<Seat> availableSeats = bookingController.getAvailableSeats(selectedFlight.getFlightNumber());
            seatsListView.setItems(FXCollections.observableArrayList(availableSeats));
            
            if (availableSeats.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Seats Available", 
                        "There are no available seats on this flight");
            }
        } else {
            seatsListView.getItems().clear();
        }
    }
    
    @FXML
    private void handleBookButton() {
        Seat selectedSeat = seatsListView.getSelectionModel().getSelectedItem();
        
        if (selectedFlight == null) {
            showAlert(Alert.AlertType.WARNING, "No Flight Selected", 
                    "Please select a flight first");
            return;
        }
        
        if (selectedSeat == null) {
            showAlert(Alert.AlertType.WARNING, "No Seat Selected", 
                    "Please select a seat");
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
    
    private void updateButtonStates() {
        boolean flightSelected = selectedFlight != null;
        boolean seatSelected = seatsListView.getSelectionModel().getSelectedItem() != null;
        
        bookButton.setDisable(!(flightSelected && seatSelected));
        seatsListView.setDisable(!flightSelected);
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