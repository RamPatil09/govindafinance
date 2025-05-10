package com.patil.govindafinance.controller;

import com.patil.govindafinance.dao.request.CustomerRequest;
import com.patil.govindafinance.dao.response.CustomerResponse;
import com.patil.govindafinance.model.Customer;
import com.patil.govindafinance.repository.CustomerRepository;
import com.patil.govindafinance.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<String> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        Optional<Customer> customer = customerRepository.findByEmail(customerRequest.getEmail());

        if (customer.isPresent()) {
            return new ResponseEntity<>("Email already registered!!", HttpStatus.BAD_REQUEST);
        } else {
            return customerService.createCustomer(customerRequest); // Already returns ResponseEntity
        }

        // Can implement sending email after registration completed
        // Can implement sending email for kyc verification completed
    }


    @GetMapping
    public ResponseEntity<CustomerResponse> getCustomerByEmail(@RequestParam String email) {
        CustomerResponse customer = customerService.getCustomerByEmail(email);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @PutMapping("/password/reset")
    public ResponseEntity<String> passwordReset(@RequestParam String email, @RequestParam String oldPassword, @RequestParam String newPassword) {
        ResponseEntity<String> stringResponseEntity = customerService.passwordReset(email, oldPassword, newPassword);
        return new ResponseEntity<>(stringResponseEntity.getBody(), stringResponseEntity.getStatusCode());
    }

}
