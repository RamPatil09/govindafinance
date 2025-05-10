package com.patil.govindafinance.service;


import com.patil.govindafinance.dao.request.CustomerRequest;
import com.patil.govindafinance.dao.response.CustomerResponse;
import org.springframework.http.ResponseEntity;

public interface CustomerService {
    ResponseEntity<String> createCustomer(CustomerRequest customerRequest);

    CustomerResponse getCustomerByEmail(String email);

    ResponseEntity<String> passwordReset(String email, String oldPassword, String password);
}
