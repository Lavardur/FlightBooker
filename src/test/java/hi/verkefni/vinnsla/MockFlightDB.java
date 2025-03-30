package hi.verkefni.vinnsla;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mock implementation of FlightDB for testing purposes.
 * This class simulates flight database operations without requiring an actual database.
 */
public class MockFlightDB extends FlightDB {
    private Map<String, Flight> flights = new HashMap<>();
    
    public MockFlightDB() {
        // Initialize with some test flights
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        addFlight(new Flight(
            "FI101", 
            "KEF", 
            "JFK", 
            LocalDateTime.parse("2020-11-01 12:00:00", formatter), 
            LocalDateTime.parse("2020-11-01 18:00:00", formatter)
        ));
        
        addFlight(new Flight(
            "FI102", 
            "JFK", 
            "KEF", 
            LocalDateTime.parse("2020-11-02 12:00:00", formatter), 
            LocalDateTime.parse("2020-11-02 18:00:00", formatter)
        ));
    }
    
    /**
     * Add a flight to the mock database
     * 
     * @param flight The flight to add
     */
    public void addFlight(Flight flight) {
        flights.put(flight.getFlightNumber(), flight);
    }
    
    /**
     * Remove a flight from the mock database
     * 
     * @param flightNumber The flight number to remove
     */
    @Override
    public Flight selectByFlightNumber(String flightNumber) {
        return flights.get(flightNumber);
    }
    
    /**
     * Search for flights based on origin, destination, and date from the mock database
     * 
     * @param origin The origin airport code (can be null)
     * @param destination The destination airport code (can be null)
     * @param date The departure date (can be null)
     * @return List of matching flights
     */
    @Override
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        return flights.values().stream()
            .filter(f -> (origin == null || f.getOrigin().equals(origin)) && 
                   (destination == null || f.getDestination().equals(destination)) &&
                   (date == null || f.getDepartureTime().toLocalDate().equals(date.toLocalDate())))
            .collect(Collectors.toList());
    }
    
    /**
     * Search for flights based on origin only from the mock database
     * 
     * @param origin The origin airport code
     * @return List of matching flights
     */
    @Override
    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights.values());
    }
    
    /**
     * Reset the mock database, clearing all flights
     */
    public void reset() {
        flights.clear();
    }
    
    /**
     * Get the number of flights in the mock database
     * 
     * @return The number of flights
     */
    public int getFlightCount() {
        return flights.size();
    }
}