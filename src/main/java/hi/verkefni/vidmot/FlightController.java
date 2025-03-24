package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Flight;
import hi.verkefni.vinnsla.FlightDB;
import java.time.LocalDateTime;
import java.util.List;

public class FlightController {
    private FlightDB flightDB;
    
    public FlightController(FlightDB flightDB) {
        this.flightDB = flightDB;
    }
    
    public Flight getFlightByNumber(String flightNumber) {
        return flightDB.selectByFlightNumber(flightNumber);
    }
    
    /**
     * Flexible flight search that handles null or empty parameters
     * 
     * @param origin Origin airport code (can be null/empty)
     * @param destination Destination airport code (can be null/empty)
     * @param date Departure date (can be null)
     * @return List of matching flights
     */
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        // Normalize empty strings to null
        if (origin != null && origin.trim().isEmpty()) origin = null;
        if (destination != null && destination.trim().isEmpty()) destination = null;
        
        return flightDB.searchFlights(origin, destination, date);
    }
    
    /**
     * Search flights by origin only
     * 
     * @param origin The origin airport code
     * @return List of matching flights
     */
    public List<Flight> searchByOrigin(String origin) {
        return flightDB.searchByOrigin(origin);
    }
    
    /**
     * Search flights by destination only
     * 
     * @param destination The destination airport code
     * @return List of matching flights
     */
    public List<Flight> searchByDestination(String destination) {
        return flightDB.searchByDestination(destination);
    }
    
    /**
     * Search flights by date only
     * 
     * @param date The departure date
     * @return List of matching flights
     */
    public List<Flight> searchByDate(LocalDateTime date) {
        return flightDB.searchByDate(date);
    }
    
    /**
     * Get all flights in the system
     * 
     * @return List of all flights
     */
    public List<Flight> getAllFlights() {
        return flightDB.getAllFlights();
    }
    
    /**
     * Get a list of all origin airports
     * 
     * @return List of airport codes
     */
    public List<String> getAllOrigins() {
        return flightDB.getAllOrigins();
    }
    
    /**
     * Get a list of all destination airports
     * 
     * @return List of airport codes
     */
    public List<String> getAllDestinations() {
        return flightDB.getAllDestinations();
    }
}
