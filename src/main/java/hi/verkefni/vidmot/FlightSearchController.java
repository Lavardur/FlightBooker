package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Flight;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlightSearchController {
    private FlightController flightController;
    private Flight selectedFlight;
    
    @FXML
    private CheckBox originCheckBox;
    
    @FXML
    private ComboBox<String> originComboBox;
    
    @FXML
    private CheckBox destinationCheckBox;
    
    @FXML
    private ComboBox<String> destinationComboBox;
    
    @FXML
    private CheckBox dateCheckBox;
    
    @FXML
    private DatePicker departureDatePicker;
    
    @FXML
    private Button searchFlightsButton;
    
    @FXML
    private Button clearSearchButton;
    
    @FXML
    private ListView<Flight> flightsListView;
    
    @FXML
    private Button selectFlightButton;
    
    @FXML
    private Button cancelButton;
    
    public FlightSearchController(FlightController flightController) {
        this.flightController = flightController;
    }
    
    @FXML
    public void initialize() {
        // Populate origin and destination dropdowns
        originComboBox.setItems(FXCollections.observableArrayList(flightController.getAllOrigins()));
        destinationComboBox.setItems(FXCollections.observableArrayList(flightController.getAllDestinations()));
        
        // Configure flight list cell factory
        setupFlightsListView();
        
        // Initial button states
        selectFlightButton.setDisable(true);
        
        // Add selection listener to enable/disable select button
        flightsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> selectFlightButton.setDisable(newVal == null));
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
    
    @FXML
    private void handleSearchOptionChanged() {
        // Enable/disable controls based on checkbox state
        originComboBox.setDisable(!originCheckBox.isSelected());
        destinationComboBox.setDisable(!destinationCheckBox.isSelected());
        departureDatePicker.setDisable(!dateCheckBox.isSelected());
        
        // Require at least one search option
        searchFlightsButton.setDisable(
            !originCheckBox.isSelected() && 
            !destinationCheckBox.isSelected() && 
            !dateCheckBox.isSelected()
        );
    }
    
    @FXML
    private void handleSearchFlights() {
        // Get search parameters (if enabled)
        String origin = originCheckBox.isSelected() ? originComboBox.getValue() : null;
        String destination = destinationCheckBox.isSelected() ? destinationComboBox.getValue() : null;
        LocalDate date = dateCheckBox.isSelected() ? departureDatePicker.getValue() : null;
        
        // Check if at least one option is selected and has a value
        boolean hasOrigin = origin != null && !origin.isEmpty();
        boolean hasDestination = destination != null && !destination.isEmpty();
        boolean hasDate = date != null;
        
        if (!hasOrigin && !hasDestination && !hasDate) {
            showAlert(Alert.AlertType.WARNING, "Search Error", 
                    "Please select at least one search option and provide a value");
            return;
        }
        
        // Convert LocalDate to LocalDateTime (start of day) if date is provided
        LocalDateTime departureDateTime = hasDate ? date.atStartOfDay() : null;
        
        // Search flights with flexible parameters
        List<Flight> flights = flightController.searchFlights(origin, destination, departureDateTime);
        
        flightsListView.setItems(FXCollections.observableArrayList(flights));
        
        if (flights.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Flights", 
                    "No flights found matching your criteria");
        }
    }
    
    @FXML
    private void handleClearSearch() {
        // Clear selections
        originCheckBox.setSelected(false);
        destinationCheckBox.setSelected(false);
        dateCheckBox.setSelected(false);
        
        originComboBox.setValue(null);
        destinationComboBox.setValue(null);
        departureDatePicker.setValue(null);
        
        // Reset controls state
        handleSearchOptionChanged();
        
        // Clear results
        flightsListView.getItems().clear();
    }
    
    @FXML
    private void handleSelectFlight() {
        selectedFlight = flightsListView.getSelectionModel().getSelectedItem();
        
        if (selectedFlight != null) {
            // Close this dialog
            Stage stage = (Stage) selectFlightButton.getScene().getWindow();
            stage.close();
        }
    }
    
    @FXML
    private void handleCancel() {
        // Reset selection and close dialog
        selectedFlight = null;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Get the flight selected by the user
     * 
     * @return The selected flight or null if cancelled
     */
    public Flight getSelectedFlight() {
        return selectedFlight;
    }
}