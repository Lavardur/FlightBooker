package hi.verkefni.vinnsla;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of CustomerDB for testing purposes.
 * This class simulates database operations without requiring an actual database.
 */
public class MockCustomerDB extends CustomerDB {
    private Map<String, Customer> customers = new HashMap<>();
    
    // Track method calls for verification in tests
    private int insertCalls = 0;
    private int updateCalls = 0;
    private int selectCalls = 0;
    private int deleteCalls = 0;
    
    @Override
    public Customer selectById(String customerId) {
        selectCalls++;
        return customers.get(customerId);
    }
    
    @Override
    public void insert(Customer customer) {
        insertCalls++;
        customers.put(customer.getCustomerId(), customer);
    }
    
    @Override
    public void update(Customer customer) {
        updateCalls++;
        customers.put(customer.getCustomerId(), customer);
    }
    
    @Override
    public void delete(String customerId) {
        deleteCalls++;
        customers.remove(customerId);
    }
    
    // Methods to help with test verification
    public int getInsertCalls() {
        return insertCalls;
    }
    
    public int getUpdateCalls() {
        return updateCalls;
    }
    
    public int getSelectCalls() {
        return selectCalls;
    }
    
    public int getDeleteCalls() {
        return deleteCalls;
    }
    
    public void reset() {
        customers.clear();
        insertCalls = 0;
        updateCalls = 0;
        selectCalls = 0;
        deleteCalls = 0;
    }
    
    public int getCustomerCount() {
        return customers.size();
    }
}