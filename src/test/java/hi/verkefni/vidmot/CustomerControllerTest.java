package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Customer;
import hi.verkefni.vinnsla.MockCustomerDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test fixture for CustomerController using JUnit 5.
 * This demonstrates testing the controller with a mock database.
 */
public class CustomerControllerTest {
    private MockCustomerDB mockDB;
    private CustomerController controller;
    
    @BeforeEach
    public void setUp() {
        // Set up a fresh mock DB and controller before each test
        mockDB = new MockCustomerDB();
        controller = new CustomerController(mockDB);
    }
    
    @Test
    public void testRegisterCustomer() {
        // Arrange
        Customer customer = new Customer("C001", "johndoe", "john@example.com");
        
        // Act
        Customer result = controller.registerCustomer(customer);
        
        // Assert
        assertEquals(1, mockDB.getInsertCalls(), "Insert method should be called once");
        assertEquals(1, mockDB.getCustomerCount(), "One customer should be in the mock database");
        assertEquals("johndoe", result.getUsername(), "Username should match");
        assertEquals("john@example.com", result.getEmail(), "Email should match");
    }
    
    @Test
    public void testUpdateCustomer() {
        // Arrange - first register a customer
        Customer customer = new Customer("C001", "johndoe", "john@example.com");
        controller.registerCustomer(customer);
        
        // Act - update the customer
        customer.setEmail("john.doe@updated.com");
        Customer updatedCustomer = controller.updateCustomer(customer);
        
        // Assert
        assertEquals(1, mockDB.getUpdateCalls(), "Update method should be called once");
        assertEquals("john.doe@updated.com", updatedCustomer.getEmail(), "Email should be updated");
        
        // Verify we can retrieve the updated customer
        Customer retrievedCustomer = controller.getCustomer("C001");
        assertEquals("john.doe@updated.com", retrievedCustomer.getEmail(), "Retrieved customer should have updated email");
    }
    
    @Test
    public void testGetCustomer() {
        // Arrange - add a customer to the mock DB
        Customer customer = new Customer("C001", "johndoe", "john@example.com");
        controller.registerCustomer(customer);
        
        // Reset call counters to clearly see the getCustomer calls
        mockDB.reset();
        mockDB.insert(customer); // Add back the customer after reset
        
        // Act
        Customer result = controller.getCustomer("C001");
        
        // Assert
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
        assertNotNull(result, "Customer should be found");
        assertEquals("johndoe", result.getUsername(), "Username should match");
    }
    
    @Test
    public void testGetCustomerNotFound() {
        // Act & Assert
        assertNull(controller.getCustomer("NONEXISTENT"), "Should return null for non-existent customer");
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
    }
}