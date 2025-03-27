package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Customer;
import hi.verkefni.vinnsla.MockCustomerDB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerControllerTest {
    private MockCustomerDB mockDB;
    private CustomerController controller;
    
    private static final String VALID_ID = "2404012073";
    private static final String INVALID_ID = "9999999999";
    
    @BeforeEach
    public void setUp() {
        mockDB = new MockCustomerDB();
        controller = new CustomerController(mockDB);
    }
    
    @AfterEach
    public void tearDown() {
        // Clean up after each test
        mockDB.reset();
        mockDB = null;
        controller = null;
    }
    
    @Test
    public void testRegisterCustomer() {
        // Arrange
        Customer customer = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        
        // Act
        controller.registerCustomer(customer);
        
        // Assert - verify DB operations
        assertEquals(1, mockDB.getInsertCalls(), "Insert method should be called once");
        assertEquals(1, mockDB.getCustomerCount(), "One customer should be in the mock database");
        
        // Most importantly - verify customer was actually persisted in DB by retrieval
        Customer retrievedCustomer = mockDB.selectById(VALID_ID);
        assertNotNull(retrievedCustomer, "Customer should exist in the database");
        assertEquals("John Doe", retrievedCustomer.getName(), "Retrieved customer name should match");
        assertEquals("john@example.com", retrievedCustomer.getEmail(), "Retrieved customer email should match");
        assertEquals("5551234", retrievedCustomer.getPhoneNumber(), "Retrieved customer phone should match");
    }
    
    @Test
    public void testRegisterCustomerAndVerifyWithController() {
        // Arrange
        Customer customer = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        
        // Act - register the customer
        controller.registerCustomer(customer);
        
        // Reset call counters to verify the retrieval operation separately
        mockDB.resetCallCounters();
        
        // Assert - verify customer can be retrieved through controller
        Customer retrievedCustomer = controller.getCustomer(VALID_ID);
        
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
        assertNotNull(retrievedCustomer, "Customer should be found");
        assertEquals("John Doe", retrievedCustomer.getName(), "Name should match");
        assertEquals("john@example.com", retrievedCustomer.getEmail(), "Email should match");
        assertEquals("5551234", retrievedCustomer.getPhoneNumber(), "Phone number should match");
    }
    
    @Test
    public void testUpdateCustomer() {
        // Arrange
        Customer customer = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        controller.registerCustomer(customer);
        
        // Reset call counters to verify the update operation separately
        mockDB.resetCallCounters();
        
        // Act - modify and update the customer
        customer.setEmail("john.doe@updated.com");
        customer.setPhoneNumber("5559876");
        controller.updateCustomer(customer);
        
        // Assert - verify DB operations
        assertEquals(1, mockDB.getUpdateCalls(), "Update method should be called once");
        assertEquals(0, mockDB.getInsertCalls(), "Insert method should not be called");
        
        // Verify customer was actually updated in DB
        Customer directlyRetrieved = mockDB.selectById(VALID_ID);
        assertEquals("john.doe@updated.com", directlyRetrieved.getEmail(), "Email should be updated in DB");
        assertEquals("5559876", directlyRetrieved.getPhoneNumber(), "Phone should be updated in DB");
        
        // Reset call counters to verify the retrieval operation separately
        mockDB.resetCallCounters();
        
        // Verify the update is persisted by retrieving through controller
        Customer retrievedCustomer = controller.getCustomer(VALID_ID);
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
        assertEquals("john.doe@updated.com", retrievedCustomer.getEmail(), "Retrieved customer should have updated email");
        assertEquals("5559876", retrievedCustomer.getPhoneNumber(), "Retrieved customer should have updated phone");
    }
    
    @Test
    public void testGetCustomer() {
        // Arrange
        Customer customer = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        mockDB.insert(customer);
        
        // Reset call counters to verify the retrieval operation separately
        mockDB.resetCallCounters();
        
        // Act
        Customer result = controller.getCustomer(VALID_ID);
        
        // Assert
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
        assertNotNull(result, "Customer should be found");
        assertEquals("John Doe", result.getName(), "Name should match");
        assertEquals("john@example.com", result.getEmail(), "Email should match");
        assertEquals("5551234", result.getPhoneNumber(), "Phone number should match");
    }
    
    @Test
    public void testGetCustomerNotFound() {
        // Act
        Customer result = controller.getCustomer(INVALID_ID);
        
        // Assert
        assertNull(result, "Should return null for non-existent customer");
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
    }
    
    @Test
    public void testMultipleCustomers_UniqueIds() {
        // Arrange - create customers with different IDs
        Customer customer1 = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        Customer customer2 = new Customer("2504012074", "Jane Smith", "jane@example.com", "5555678");
        
        // Act - register both customers
        controller.registerCustomer(customer1);
        controller.registerCustomer(customer2);
        
        // Assert - verify both are persisted and retrievable by their IDs
        assertEquals(2, mockDB.getCustomerCount(), "Two customers should be in the database");
        
        assertNotNull(mockDB.selectById(VALID_ID), "First customer should be retrievable by ID");
        assertNotNull(mockDB.selectById("2504012074"), "Second customer should be retrievable by ID");
        
        // Verify no ID collision or overwriting
        assertEquals("John Doe", mockDB.selectById(VALID_ID).getName(), "First customer should maintain identity");
        assertEquals("Jane Smith", mockDB.selectById("2504012074").getName(), "Second customer should maintain identity");
    }
}