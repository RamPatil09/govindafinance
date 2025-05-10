package com.patil.govindafinance.service;


import com.mongodb.DuplicateKeyException;
import com.patil.govindafinance.dao.request.CustomerRequest;
import com.patil.govindafinance.dao.response.CustomerResponse;
import com.patil.govindafinance.enums.KycIdType;
import com.patil.govindafinance.model.Address;
import com.patil.govindafinance.model.Customer;
import com.patil.govindafinance.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final JavaMailSender javaMailSender;

    public CustomerServiceImpl(CustomerRepository customerRepository, JavaMailSender javaMailSender) {
        this.customerRepository = customerRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ResponseEntity<String> createCustomer(CustomerRequest customerRequest) {
        try {
            // Check for duplicate KYC ID
            Optional<Customer> existingKyc = customerRepository.findByKycIdNumber(customerRequest.getKycIdNumber());
            if (existingKyc.isPresent()) {
                log.warn("KYC ID already exists: {}", customerRequest.getKycIdNumber());
                return new ResponseEntity<>("This KYC ID is already in use. If you believe this is a mistake, please reach out to our support team.", HttpStatus.CONFLICT);
            }

            Address address = Address.builder()
                    .street(customerRequest.getAddress().getStreet())
                    .city(customerRequest.getAddress().getCity())
                    .state(customerRequest.getAddress().getState())
                    .country(customerRequest.getAddress().getCountry())
                    .postalCode(customerRequest.getAddress().getPostalCode())
                    .build();

            String password = customerRequest.getFirstName() + "@123";
            log.info("Inside Create customer function.");

            KycIdType kycIdType;
            switch (customerRequest.getKycIdType().toLowerCase()) {
                case "aadharcard":
                    kycIdType = KycIdType.AADHARCARD;
                    break;
                case "pancard":
                    kycIdType = KycIdType.PANCARD;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid KYC ID Type: " + customerRequest.getKycIdType());
            }

            Customer customer = Customer.builder()
                    .fullName(customerRequest.getFirstName() + " " + customerRequest.getLastName())
                    .email(customerRequest.getEmail())
                    .phoneNumber(customerRequest.getPhoneNumber())
                    .password(password)
                    .address(address)
                    .dateOfBirth(customerRequest.getDateOfBirth())
                    .kycIdType(kycIdType)
                    .kycIdNumber(customerRequest.getKycIdNumber())
                    .registeredOn(LocalDate.now())
                    .build();

            log.info("Saving customer details.");
            Customer saved = customerRepository.save(customer);

            sendPasswordResetEmail(saved.getEmail());
            return new ResponseEntity<>("Customer created successfully with ID: " + saved.getId(), HttpStatus.CREATED);

        } catch (DuplicateKeyException ex) {
            log.error("Duplicate KYC ID Number: {}", customerRequest.getKycIdNumber());
            return new ResponseEntity<>("Customer with the given KYC ID already exists.", HttpStatus.CONFLICT);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error while creating customer", e);
            return new ResponseEntity<>("Something went wrong while creating the customer.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public CustomerResponse getCustomerByEmail(String email) {
        try {
            Optional<Customer> customer = customerRepository.findByEmail(email);

            CustomerResponse customerResponse = CustomerResponse.builder()
                    .fullName(customer.get().getFullName())
                    .email(customer.get().getEmail())
                    .phoneNumber(customer.get().getPhoneNumber())
                    .address(customer.get().getAddress())
                    .dateOfBirth(customer.get().getDateOfBirth())
                    .kycIdType(customer.get().getKycIdType().toString())
                    .kycIdNumber(customer.get().getKycIdNumber())
                    .build();

            return customerResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ResponseEntity<String> passwordReset(String email, String oldPassword, String password) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent() && oldPassword.equals(customer.get().getPassword())) {
            Customer customer1 = customer.get();
            customer1.setPassword(password);
            customerRepository.save(customer1);
            return new ResponseEntity<>("Password reset successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found with the provided email.", HttpStatus.BAD_REQUEST);
        }
    }

    private void sendPasswordResetEmail(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);

        if (customer.isPresent()) {
            String toEmail = customer.get().getEmail();
            String subject = "Welcome Aboard! Here’s How to Access Your Govinda Finance Account";
            String body = "Hi " + customer.get().getFullName() + ",\n\n" +
                    "Welcome to Govinda Finance! You have successfully registered with us.\n\n" +
                    "Your username and password are as follows:\n\n" +
                    "Username: " + customer.get().getEmail() + "\n" +
                    "Password: " + customer.get().getPassword() + " \n\n" +
                    "To access your account, please use the above username and password to log in.\n\n" +
                    "For security reasons, we recommend that you change your password immediately after logging in.\n\n" +
                    "To change your password, click the link below:\n\n" +
                    "\uD83D\uDC49 [Change Your Password](#)\n\n" +
                    "If you did not request this email, please ignore it.\n\n" +
                    "Thank you,\n" +
                    "The Govinda Finance Team\n";

            String fromEmail = "ramp051346@gmail.com";

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            mailMessage.setFrom(fromEmail);

            javaMailSender.send(mailMessage);
        } else {
            throw new RuntimeException("Customer not found with Email: " + email);
        }
    }

}
