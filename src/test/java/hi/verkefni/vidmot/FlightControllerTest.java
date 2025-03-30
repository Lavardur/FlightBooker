package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Flight;
import hi.verkefni.vinnsla.MockFlightDB;
import org.junit.jupiter.api.AfterEach;
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
    
    @AfterEach
    public void tearDown() {
        mockDB.reset();
        mockDB = null;
        controller = null;
    }
    
    @Test
    public void testGetFlightByNumber() {
        // Act
        Flight flight = controller.getFlightByNumber("FI101");
        
        // Assert
        assertNotNull(flight, "Flight should be found");
        assertEquals("FI101", flight.getFlightNumber(), "Flight number should match");
    }
    
    @Test
    public void testGetFlightByNumberNotFound() {
        // Act
        Flight flight = controller.getFlightByNumber("NONEXISTENT");
        
        // Assert
        assertNull(flight, "Flight should not be found");
    }
    
    @Test
    public void testSearchFlights() {
        // Arrange
        LocalDateTime searchDate = LocalDateTime.parse("2020-11-01 00:00:00", formatter);
        
        // Act
        List<Flight> flights = controller.searchFlights("KEF", "JFK", searchDate);
        
        // Assert
        assertEquals(1, flights.size(), "One flight should be found");
        assertEquals("FI101", flights.get(0).getFlightNumber(), "Should find the correct flight");
    }
    
    @Test
    public void testSearchFlightsNoResults() {
        // Arrange
        LocalDateTime searchDate = LocalDateTime.parse("2020-11-10 00:00:00", formatter);
        
        // Act
        List<Flight> flights = controller.searchFlights("KEF", "JFK", searchDate);
        
        // Assert
        assertTrue(flights.isEmpty(), "No flights should be found");
    }
    
    @Test
    public void testSearchFlightsByOriginOnly() {
        // Act
        List<Flight> flights = controller.searchFlights("KEF", null, null);
        
        // Assert
        assertEquals(1, flights.size(), "One flight should be found");
        assertEquals("FI101", flights.get(0).getFlightNumber(), "Should find flight from KEF");
    }
    
    @Test
    public void testSearchFlightsByDestinationOnly() {
        // Act
        List<Flight> flights = controller.searchFlights(null, "KEF", null);
        
        // Assert
        assertEquals(1, flights.size(), "One flight should be found");
        assertEquals("FI102", flights.get(0).getFlightNumber(), "Should find flight to KEF");
    }
    
    @Test
    public void testGetAllFlights() {
        // Act
        List<Flight> flights = controller.getAllFlights();
        
        // Assert
        assertEquals(2, flights.size(), "Two flights should be found");
        assertTrue(
            flights.stream().map(Flight::getFlightNumber).anyMatch(num -> num.equals("FI101")) &&
            flights.stream().map(Flight::getFlightNumber).anyMatch(num -> num.equals("FI102")),
            "Both test flights should be in the results"
        );
    }
    
    @Test
    public void testAddFlightAndRetrieve() {
        // Arrange
        Flight newFlight = new Flight(
            "FI103",
            "KEF",
            "CPH",
            LocalDateTime.parse("2020-11-03 10:00:00", formatter),
            LocalDateTime.parse("2020-11-03 13:30:00", formatter)
        );
        mockDB.addFlight(newFlight);
        
        // Act
        Flight retrievedFlight = controller.getFlightByNumber("FI103");
        
        // Assert
        assertNotNull(retrievedFlight, "Added flight should be retrievable");
        // Only check one additional attribute as a sanity check
        assertEquals("CPH", retrievedFlight.getDestination(), "Destination should be preserved");
    }
}