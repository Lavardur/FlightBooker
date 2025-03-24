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
    
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        return flightDB.searchFlights(origin, destination, date);
    }
    
    public List<Flight> getAllFlights() {
        return flightDB.getAllFlights();
    }
}
