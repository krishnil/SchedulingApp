package model;

/**
 *
 * @author krish
 */
public class Customer {
    
    private int customerId;
    private String customerName;
    private String address;
    private String address2;
    private int countryId;
    private String city;
    private int postalCode;
    private String phone;
    private boolean active;

    public Customer(int customerId, String customerName, String address, String address2, int countryId, String city, int postalCode, String phone, boolean active) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.address2 = address2;
        this.countryId = countryId;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
        this.active = active;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }   
    
}
