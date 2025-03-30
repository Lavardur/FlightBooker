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
        // Initialize the mock database and controller before each test
        mockDB = new MockCustomerDB();
        controller = new CustomerController(mockDB);
    }
    
    @AfterEach
    public void tearDown() {
        // Clean up after each test
        mockDB = null;
        controller = null;
    }
    
    @Test
    public void testRegisterCustomer() {
        
        Customer customer = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        
        controller.registerCustomer(customer);
        
        Customer retrievedCustomer = mockDB.selectById(VALID_ID);
        assertNotNull(retrievedCustomer, "Customer should exist in the database");
    }
    
    @Test
    public void testUpdateCustomer() {

        Customer customer = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        controller.registerCustomer(customer);
        
        customer.setEmail("john.doe@updated.com");
        customer.setPhoneNumber("5559876");
        controller.updateCustomer(customer);
        
        Customer retrievedCustomer = controller.getCustomer(VALID_ID);
        assertEquals("john.doe@updated.com", retrievedCustomer.getEmail(), "Retrieved customer should have updated email");
        assertEquals("5559876", retrievedCustomer.getPhoneNumber(), "Retrieved customer should have updated phone");
    }
    
    @Test
    public void testGetCustomer() {
        Customer customer = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        mockDB.insert(customer);
        
        Customer result = controller.getCustomer(VALID_ID);
        
        assertNotNull(result, "Customer should be found");
    }
    
    @Test
    public void testGetCustomerNotFound() {
        Customer result = controller.getCustomer(INVALID_ID);
        
        assertNull(result, "Should return null for non-existent customer");
    }
    
    @Test
    public void testMultipleCustomers_UniqueIds() {
        // create customers with different IDs
        Customer customer1 = new Customer(VALID_ID, "John Doe", "john@example.com", "5551234");
        Customer customer2 = new Customer("2504012074", "Jane Smith", "jane@example.com", "5555678");
        
        // register both customers
        controller.registerCustomer(customer1);
        controller.registerCustomer(customer2);
        
        // verify both are persisted and retrievable by their IDs
        assertEquals(2, mockDB.getCustomerCount(), "Two customers should be in the database");
        assertNotNull(mockDB.selectById(VALID_ID), "First customer should be retrievable by ID");
        assertNotNull(mockDB.selectById("2504012074"), "Second customer should be retrievable by ID");
        
        // Verify no ID collision or overwriting
        assertEquals("John Doe", mockDB.selectById(VALID_ID).getName(), "First customer should maintain identity");
        assertEquals("Jane Smith", mockDB.selectById("2504012074").getName(), "Second customer should maintain identity");
    }
}