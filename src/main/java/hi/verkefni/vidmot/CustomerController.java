package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Customer;
import hi.verkefni.vinnsla.CustomerDB;

// CustomerController
// + registerCustomer(customer: Customer) : void
// + updateCustomer(customer: Customer) : void
// + getCustomer(customerId: String) : Customer

// CustomerDB
// + selectById(customerId: String) : Customer
// + insert(customer: Customer) : void
// + update(customer: Customer) : void
// + delete(customerId: String) : void

public class CustomerController {
    private CustomerDB customerDB;
    
    public CustomerController(CustomerDB customerDB) {
        this.customerDB = customerDB;
    }
    
    /**
     * Register a new customer in the system
     * 
     * @param customer The customer to register
     */
    public void registerCustomer(Customer customer) {
        customerDB.insert(customer);
    }
    
    /**
     * Update an existing customer's information
     * 
     * @param customer The customer with updated information
     */
    public void updateCustomer(Customer customer) {
        customerDB.update(customer);
    }
    
    /**
     * Get a customer by their ID
     * 
     * @param customerId The ID of the customer to retrieve
     * @return The customer if found, null otherwise
     */
    public Customer getCustomer(String customerId) {
        return customerDB.selectById(customerId);
    }
}
