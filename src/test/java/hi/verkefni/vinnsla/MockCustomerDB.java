package hi.verkefni.vinnsla;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of CustomerDB for testing purposes.
 * This class simulates database operations without requiring an actual database.
 */
public class MockCustomerDB extends CustomerDB {
    private Map<String, Customer> customers = new HashMap<>();
    
    @Override
    public Customer selectById(String customerId) {
        return customers.get(customerId);
    }
    
    @Override
    public void insert(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }
    
    @Override
    public void update(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }
    
    @Override
    public void delete(String customerId) {
        customers.remove(customerId);
    }
    
    public int getCustomerCount() {
        return customers.size();
    }
}