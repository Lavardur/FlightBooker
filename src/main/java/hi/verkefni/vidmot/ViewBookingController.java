package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ViewBookingController {
    private BookingController bookingController;
    private FlightController flightController;
    private CustomerController customerController;
    private String bookingId;
    private Booking booking;
    private boolean bookingUpdated = false;
    
    @FXML
    private Label bookingIdLabel;
    
    @FXML
    private Label bookingDateLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label customerNameLabel;
    
    @FXML
    private Label customerEmailLabel;
    
    @FXML
    private Label customerPhoneLabel;
    
    @FXML
    private Label flightNumberLabel;
    
    @FXML
    private Label flightRouteLabel;
    
    @FXML
    private Label departureTimeLabel;
    
    @FXML
    private Label arrivalTimeLabel;
    
    @FXML
    private Label currentSeatLabel;
    
    @FXML
    private ComboBox<Seat> availableSeatsComboBox;
    
    @FXML
    private Button changeSeatButton;
    
    @FXML
    private Button cancelBookingButton;
    
    @FXML
    private Button closeButton;
    
    public ViewBookingController(BookingController bookingController, 
                               FlightController flightController,
                               CustomerController customerController,
                               String bookingId) {
        this.bookingController = bookingController;
        this.flightController = flightController;
        this.customerController = customerController;
        this.bookingId = bookingId;
    }
    
    @FXML
    public void initialize() {
        // Load booking details
        booking = bookingController.viewBooking(bookingId);
        
        if (booking == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Booking not found");
            return;
        }
        
        // Load related data
        Customer customer = customerController.getCustomer(booking.getCustomerId());
        Flight flight = flightController.getFlightByNumber(booking.getFlightNumber());
        
        // Set data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        bookingIdLabel.setText(booking.getBookingId());
        bookingDateLabel.setText(booking.getBookingDate().format(formatter));
        statusLabel.setText(booking.getStatus());
        
        if (customer != null) {
            customerNameLabel.setText(customer.getName());
            customerEmailLabel.setText(customer.getEmail());
            customerPhoneLabel.setText(customer.getPhoneNumber());
        }
        
        if (flight != null) {
            flightNumberLabel.setText(flight.getFlightNumber());
            flightRouteLabel.setText(flight.getOrigin() + " to " + flight.getDestination());
            departureTimeLabel.setText(flight.getDepartureTime().format(formatter));
            arrivalTimeLabel.setText(flight.getArrivalTime().format(formatter));
        }
        
        currentSeatLabel.setText(booking.getSeatNumber());
        
        // Load available seats
        loadAvailableSeats();
        
        // Set button states based on booking status
        boolean isConfirmed = "CONFIRMED".equals(booking.getStatus());
        changeSeatButton.setDisable(!isConfirmed);
        cancelBookingButton.setDisable(!isConfirmed);
    }
    
    private void loadAvailableSeats() {
        List<Seat> availableSeats = bookingController.getAvailableSeats(booking.getFlightNumber());
        availableSeatsComboBox.setItems(FXCollections.observableArrayList(availableSeats));
        
        // Set cell factory to display seat numbers properly
        availableSeatsComboBox.setCellFactory(param -> new ListCell<Seat>() {
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
        
        // Set button converter for display in combo box
        availableSeatsComboBox.setButtonCell(new ListCell<Seat>() {
            @Override
            protected void updateItem(Seat seat, boolean empty) {
                super.updateItem(seat, empty);
                
                if (empty || seat == null) {
                    setText("Select a Seat");
                } else {
                    setText("Seat " + seat.getSeatNumber());
                }
            }
        });
    }
    
    @FXML
    private void handleChangeSeat() {
        Seat selectedSeat = availableSeatsComboBox.getValue();
        
        if (selectedSeat == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a new seat");
            return;
        }
        
        try {
            bookingController.updateBooking(booking.getBookingId(), selectedSeat);
            bookingUpdated = true;
            
            // Refresh booking details
            booking = bookingController.viewBooking(bookingId);
            currentSeatLabel.setText(booking.getSeatNumber());
            
            // Reload available seats
            loadAvailableSeats();
            
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Seat changed successfully to " + selectedSeat.getSeatNumber());
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Error changing seat: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelBooking() {
        // Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure you want to cancel this booking?");
        alert.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = bookingController.cancelBooking(booking.getBookingId());
            
            if (success) {
                bookingUpdated = true;
                
                // Refresh booking details
                booking = bookingController.viewBooking(bookingId);
                statusLabel.setText(booking.getStatus());
                
                // Update button states
                changeSeatButton.setDisable(true);
                cancelBookingButton.setDisable(true);
                
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Booking cancelled successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                        "Failed to cancel booking");
            }
        }
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public boolean isBookingUpdated() {
        return bookingUpdated;
    }
}