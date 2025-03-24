package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Flight;
import hi.verkefni.vinnsla.MockFlightDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlightControllerTest {
    private MockFlightDB mockDB;
    private FlightController controller;
    private DateTimeFormatter formatter;
    
    @BeforeEach
    public void setUp() {
        mockDB = new MockFlightDB();
        controller = new FlightController(mockDB);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    @Test
    public void testGetFlightByNumber() {
        Flight flight = controller.getFlightByNumber("FI101");
        
        assertEquals(1, mockDB.getSelectByFlightNumberCalls(), "Select method should be called once");
        assertNotNull(flight, "Flight should be found");
        assertEquals("KEF", flight.getOrigin(), "Origin should match");
        assertEquals("JFK", flight.getDestination(), "Destination should match");
    }
    
    @Test
    public void testGetFlightByNumberNotFound() {
        Flight flight = controller.getFlightByNumber("NONEXISTENT");
        
        assertEquals(1, mockDB.getSelectByFlightNumberCalls(), "Select method should be called once");
        assertNull(flight, "Flight should not be found");
    }
    
    @Test
    public void testSearchFlights() {
        LocalDateTime searchDate = LocalDateTime.parse("2020-11-01 00:00:00", formatter);
        List<Flight> flights = controller.searchFlights("KEF", "JFK", searchDate);
        
        assertEquals(1, mockDB.getSearchFlightsCalls(), "Search method should be called once");
        assertEquals(1, flights.size(), "One flight should be found");
        assertEquals("FI101", flights.get(0).getFlightNumber(), "Flight number should match");
    }
    
    @Test
    public void testSearchFlightsNoResults() {
        LocalDateTime searchDate = LocalDateTime.parse("2020-11-10 00:00:00", formatter);
        List<Flight> flights = controller.searchFlights("KEF", "JFK", searchDate);
        
        assertEquals(1, mockDB.getSearchFlightsCalls(), "Search method should be called once");
        assertEquals(0, flights.size(), "No flights should be found");
    }
    
    @Test
    public void testGetAllFlights() {
        List<Flight> flights = controller.getAllFlights();
        
        assertEquals(1, mockDB.getGetAllFlightsCalls(), "GetAll method should be called once");
        assertEquals(2, flights.size(), "Two flights should be found");
    }
}