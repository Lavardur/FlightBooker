package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Customer;
import hi.verkefni.vinnsla.MockCustomerDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerControllerTest {
    private MockCustomerDB mockDB;
    private CustomerController controller;
    
    @BeforeEach
    public void setUp() {
        mockDB = new MockCustomerDB();
        controller = new CustomerController(mockDB);
    }
    
    @Test
    public void testRegisterCustomer() {
        Customer customer = new Customer("2404012073", "John Doe", "john@example.com", "5551234");
        
        Customer result = controller.registerCustomer(customer);
        
        assertEquals(1, mockDB.getInsertCalls(), "Insert method should be called once");
        assertEquals(1, mockDB.getCustomerCount(), "One customer should be in the mock database");
        assertEquals("John Doe", result.getName(), "Name should match");
        assertEquals("john@example.com", result.getEmail(), "Email should match");
        assertEquals("5551234", result.getPhoneNumber(), "Phone number should match");
    }
    
    @Test
    public void testUpdateCustomer() {
        Customer customer = new Customer("2404012073", "John Doe", "john@example.com", "5551234");
        controller.registerCustomer(customer);
        
        customer.setEmail("john.doe@updated.com");
        customer.setPhoneNumber("5559876");
        Customer updatedCustomer = controller.updateCustomer(customer);
        
        assertEquals(1, mockDB.getUpdateCalls(), "Update method should be called once");
        assertEquals("john.doe@updated.com", updatedCustomer.getEmail(), "Email should be updated");
        assertEquals("5559876", updatedCustomer.getPhoneNumber(), "Phone number should be updated");
        
        Customer retrievedCustomer = controller.getCustomer("2404012073");
        assertEquals("john.doe@updated.com", retrievedCustomer.getEmail(), "Retrieved customer should have updated email");
        assertEquals("5559876", retrievedCustomer.getPhoneNumber(), "Retrieved customer should have updated phone");
    }
    
    @Test
    public void testGetCustomer() {
        Customer customer = new Customer("2404012073", "John Doe", "john@example.com", "5551234");
        controller.registerCustomer(customer);
        
        mockDB.reset();
        mockDB.insert(customer);
        
        Customer result = controller.getCustomer("2404012073");
        
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
        assertNotNull(result, "Customer should be found");
        assertEquals("John Doe", result.getName(), "Name should match");
        assertEquals("5551234", result.getPhoneNumber(), "Phone number should match");
    }
    
    @Test
    public void testGetCustomerNotFound() {
        assertNull(controller.getCustomer("NONEXISTENT"), "Should return null for non-existent customer");
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
    }
}