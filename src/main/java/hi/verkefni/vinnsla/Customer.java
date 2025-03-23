package hi.verkefni.vinnsla;

public class Customer {
    private String customerId;
    private String username;
    private String email;

    public Customer(String customerId, String username, String email) {
        this.customerId = customerId;
        this.username = username;
        this.email = email;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}