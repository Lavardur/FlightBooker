package hi.verkefni.vinnsla;

import java.util.HashMap;
import java.util.Map;

/**
 * # Mock Object Documentation: MockCustomerDB
 *
 * ## Purpose
 * The MockCustomerDB class serves as a test double that simulates a database component without requiring an actual database connection. 
 * It implements the same interface as the real CustomerDB but stores data in memory and tracks method invocations for verification in tests.
 *
 * ## Implementation Details
 * - Extends CustomerDB to maintain the same interface
 * - Uses a HashMap to store customer data in memory
 * - Maintains counters to track how many times each database operation is called
 * - Provides additional methods for test verification (getInsertCalls, reset, etc.)
 *
 * ## Usage in Test Fixture
 * The MockCustomerDB is initialized in the @BeforeEach method of the test fixture and injected into the CustomerController, 
 * allowing tests to verify both the behavior of the controller and its interactions with the database layer.
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