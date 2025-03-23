package hi.verkefni.vidmot;

import hi.verkefni.vinnsla.Customer;
import hi.verkefni.vinnsla.CustomerDB;

// CustomerController
// + registerCustomer(customer: Customer) : Customer
// + updateCustomer(customer: Customer) : Customer
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
    
    public Customer registerCustomer(Customer customer) {
        customerDB.insert(customer);
        return customer;
    }
    
    public Customer updateCustomer(Customer customer) {
        customerDB.update(customer);
        return customer;
    }
    
    public Customer getCustomer(String customerId) {
        return customerDB.selectById(customerId);
    }
}
