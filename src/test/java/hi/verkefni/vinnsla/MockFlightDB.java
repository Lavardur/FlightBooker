package hi.verkefni.vinnsla;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MockFlightDB extends FlightDB {
    private Map<String, Flight> flights = new HashMap<>();
    
    // Track method calls for verification in tests
    private int selectByFlightNumberCalls = 0;
    private int searchFlightsCalls = 0;
    private int getAllFlightsCalls = 0;
    
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
    
    public void addFlight(Flight flight) {
        flights.put(flight.getFlightNumber(), flight);
    }
    
    @Override
    public Flight selectByFlightNumber(String flightNumber) {
        selectByFlightNumberCalls++;
        return flights.get(flightNumber);
    }
    
    @Override
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        searchFlightsCalls++;
        return flights.values().stream()
            .filter(f -> f.getOrigin().equals(origin) && 
                   f.getDestination().equals(destination) &&
                   f.getDepartureTime().toLocalDate().equals(date.toLocalDate()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Flight> getAllFlights() {
        getAllFlightsCalls++;
        return new ArrayList<>(flights.values());
    }
    
    // Methods to help with test verification
    public int getSelectByFlightNumberCalls() {
        return selectByFlightNumberCalls;
    }
    
    public int getSearchFlightsCalls() {
        return searchFlightsCalls;
    }
    
    public int getGetAllFlightsCalls() {
        return getAllFlightsCalls;
    }
    
    public void reset() {
        flights.clear();
        selectByFlightNumberCalls = 0;
        searchFlightsCalls = 0;
        getAllFlightsCalls = 0;
    }
    
    public int getFlightCount() {
        return flights.size();
    }
}