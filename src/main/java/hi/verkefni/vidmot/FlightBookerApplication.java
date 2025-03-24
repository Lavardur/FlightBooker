package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class FlightBookerApplication extends Application {
    // Controllers
    private CustomerController customerController;
    private FlightController flightController;
    private BookingController bookingController;
    
    // UI components
    @FXML
    private TextField customerIdTextField;
    
    @FXML
    private Label customerNameLabel;
    
    @FXML
    private VBox customerInfoBox;
    
    @FXML
    private ListView<Booking> bookingsListView;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button createBookingButton;
    
    @FXML
    private Button viewBookingButton;
    
    @FXML
    private Button cancelBookingButton;
    
    @FXML
    private Button searchFlightsButton;
    
    // Data
    private Customer currentCustomer;
    private ObservableList<Booking> bookingsObservableList = FXCollections.observableArrayList();
    
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database
        DatabaseInitializer.initialize();
        
        // Initialize controllers
        CustomerDB customerDB = new CustomerDB();
        FlightDB flightDB = new FlightDB();
        BookingDB bookingDB = new BookingDB();
        
        customerController = new CustomerController(customerDB);
        flightController = new FlightController(flightDB);
        bookingController = new BookingController(bookingDB, flightController, customerController);
        
        // Load UI
        FXMLLoader fxmlLoader = new FXMLLoader(FlightBookerApplication.class.getResource("/main-view.fxml"));
        fxmlLoader.setController(this);
        Parent root = fxmlLoader.load();
        
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("Flight Booker");
        stage.setScene(scene);
        stage.show();
        
        // Setup UI components
        setupBookingsListView();
        updateUIState();
    }
    
    @FXML
    public void initialize() {
        // Bind the observable list to the ListView
        bookingsListView.setItems(bookingsObservableList);
        
        // Disable buttons until a customer is selected
        updateUIState();
    }
    
    private void setupBookingsListView() {
        // Define how each booking should be displayed in the list
        bookingsListView.setCellFactory(new Callback<ListView<Booking>, ListCell<Booking>>() {
            @Override
            public ListCell<Booking> call(ListView<Booking> param) {
                return new ListCell<Booking>() {
                    @Override
                    protected void updateItem(Booking booking, boolean empty) {
                        super.updateItem(booking, empty);
                        
                        if (empty || booking == null) {
                            setText(null);
                        } else {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                            Flight flight = flightController.getFlightByNumber(booking.getFlightNumber());
                            
                            String flightInfo = flight != null ? 
                                flight.getOrigin() + " to " + flight.getDestination() : 
                                booking.getFlightNumber();
                            
                            setText(String.format("Booking #%s - %s - Flight %s (%s) - Seat %s - %s", 
                                      booking.getBookingId(),
                                      booking.getBookingDate().format(formatter),
                                      booking.getFlightNumber(),
                                      flightInfo,
                                      booking.getSeatNumber(),
                                      booking.getStatus()));
                        }
                    }
                };
            }
        });
    }
    
    @FXML
    private void handleSearchButton() {
        String customerId = customerIdTextField.getText().trim();
        
        if (customerId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a customer ID");
            return;
        }
        
        // Lookup customer
        Customer customer = customerController.getCustomer(customerId);
        
        if (customer == null) {
            showAlert(Alert.AlertType.WARNING, "Customer Not Found", 
                    "No customer found with ID: " + customerId);
            clearCustomerInfo();
            return;
        }
        
        // Set current customer and update UI
        currentCustomer = customer;
        customerNameLabel.setText("Customer: " + customer.getName());
        
        // Display customer info
        updateCustomerInfo(customer);
        
        // Get and display bookings
        List<Booking> customerBookings = bookingController.getBookingsByCustomer(customerId);
        
        bookingsObservableList.clear();
        bookingsObservableList.addAll(customerBookings);
        
        // Update UI state
        updateUIState();
    }
    
    private void updateCustomerInfo(Customer customer) {
        customerInfoBox.getChildren().clear();
        
        // Add customer details
        Label nameLabel = new Label("Name: " + customer.getName());
        Label emailLabel = new Label("Email: " + customer.getEmail());
        Label phoneLabel = new Label("Phone: " + customer.getPhoneNumber());
        
        customerInfoBox.getChildren().addAll(nameLabel, emailLabel, phoneLabel);
    }
    
    private void clearCustomerInfo() {
        currentCustomer = null;
        customerNameLabel.setText("Customer: Not selected");
        customerInfoBox.getChildren().clear();
        bookingsObservableList.clear();
        updateUIState();
    }
    
    @FXML
    private void handleCreateBooking() {
        if (currentCustomer == null) {
            showAlert(Alert.AlertType.WARNING, "No Customer Selected", 
                    "Please search for a customer first");
            return;
        }
        
        try {
            // Open the booking creation dialog/window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/create-booking-view.fxml"));
            CreateBookingController controller = new CreateBookingController(
                    bookingController, flightController, currentCustomer);
            loader.setController(controller);
            
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Create New Booking");
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with parent window
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh bookings list if a booking was created
            if (controller.isBookingCreated()) {
                refreshBookingsList();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open booking creation dialog");
        }
    }
    
    @FXML
    private void handleViewBooking() {
        Booking selectedBooking = bookingsListView.getSelectionModel().getSelectedItem();
        
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Booking Selected", 
                    "Please select a booking to view");
            return;
        }
        
        try {
            // Open the booking details dialog/window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view-booking-view.fxml"));
            ViewBookingController controller = new ViewBookingController(
                    bookingController, flightController, customerController, selectedBooking.getBookingId());
            loader.setController(controller);
            
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Booking Details");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh bookings if needed
            if (controller.isBookingUpdated()) {
                refreshBookingsList();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open booking details dialog");
        }
    }
    
    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsListView.getSelectionModel().getSelectedItem();
        
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Booking Selected", 
                    "Please select a booking to cancel");
            return;
        }
        
        // Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure you want to cancel this booking?");
        alert.setContentText("Booking ID: " + selectedBooking.getBookingId() + 
                          "\nFlight: " + selectedBooking.getFlightNumber() + 
                          "\nSeat: " + selectedBooking.getSeatNumber());
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = bookingController.cancelBooking(selectedBooking.getBookingId());
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Booking cancelled successfully");
                refreshBookingsList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                        "Failed to cancel booking");
            }
        }
    }
    
    @FXML
    private void handleSearchFlights() {
        try {
            // Open the flight search dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/flight-search-view.fxml"));
            FlightSearchController controller = new FlightSearchController(flightController);
            loader.setController(controller);
            
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Search Flights");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Get the selected flight if any
            Flight selectedFlight = controller.getSelectedFlight();
            
            if (selectedFlight != null) {
                // Do something with the selected flight
                // For example, show flight details
                showFlightDetails(selectedFlight);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open flight search dialog");
        }
    }

    private void showFlightDetails(Flight flight) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Flight Details");
        alert.setHeaderText("Flight " + flight.getFlightNumber());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        String content = String.format(
            "From: %s\nTo: %s\nDeparture: %s\nArrival: %s",
            flight.getOrigin(),
            flight.getDestination(),
            flight.getDepartureTime().format(formatter),
            flight.getArrivalTime().format(formatter)
        );
        
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void refreshBookingsList() {
        if (currentCustomer != null) {
            List<Booking> customerBookings = bookingController.getBookingsByCustomer(
                    currentCustomer.getCustomerId());
            
            bookingsObservableList.clear();
            bookingsObservableList.addAll(customerBookings);
        }
    }
    
    private void updateUIState() {
        boolean customerSelected = currentCustomer != null;
        boolean bookingSelected = bookingsListView.getSelectionModel().getSelectedItem() != null;
        
        // Enable/disable buttons based on state
        createBookingButton.setDisable(!customerSelected);
        viewBookingButton.setDisable(!bookingSelected);
        cancelBookingButton.setDisable(!bookingSelected);
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch();
    }
}