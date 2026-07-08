package com.madfood.service;

import com.madfood.entity.Customer;
import com.madfood.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customerDetails.getDietPreference() != null) customer.setDietPreference(customerDetails.getDietPreference());
        if (customerDetails.getLatitude() != null) customer.setLatitude(customerDetails.getLatitude());
        if (customerDetails.getLongitude() != null) customer.setLongitude(customerDetails.getLongitude());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
