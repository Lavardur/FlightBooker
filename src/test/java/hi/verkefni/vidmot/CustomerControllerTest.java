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

        Customer customer = new Customer("C001", "johndoe", "john@example.com");
        
        Customer result = controller.registerCustomer(customer);
        
        assertEquals(1, mockDB.getInsertCalls(), "Insert method should be called once");
        assertEquals(1, mockDB.getCustomerCount(), "One customer should be in the mock database");
        assertEquals("johndoe", result.getUsername(), "Username should match");
        assertEquals("john@example.com", result.getEmail(), "Email should match");
    }
    
    @Test
    public void testUpdateCustomer() {
        Customer customer = new Customer("C001", "johndoe", "john@example.com");
        controller.registerCustomer(customer);
        
        customer.setEmail("john.doe@updated.com");
        Customer updatedCustomer = controller.updateCustomer(customer);
        
        assertEquals(1, mockDB.getUpdateCalls(), "Update method should be called once");
        assertEquals("john.doe@updated.com", updatedCustomer.getEmail(), "Email should be updated");
        
        Customer retrievedCustomer = controller.getCustomer("C001");
        assertEquals("john.doe@updated.com", retrievedCustomer.getEmail(), "Retrieved customer should have updated email");
    }
    
    @Test
    public void testGetCustomer() {
        Customer customer = new Customer("C001", "johndoe", "john@example.com");
        controller.registerCustomer(customer);
        
        mockDB.reset();
        mockDB.insert(customer);
        
        Customer result = controller.getCustomer("C001");
        
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
        assertNotNull(result, "Customer should be found");
        assertEquals("johndoe", result.getUsername(), "Username should match");
    }
    
    @Test
    public void testGetCustomerNotFound() {
        assertNull(controller.getCustomer("NONEXISTENT"), "Should return null for non-existent customer");
        assertEquals(1, mockDB.getSelectCalls(), "Select method should be called once");
    }
}