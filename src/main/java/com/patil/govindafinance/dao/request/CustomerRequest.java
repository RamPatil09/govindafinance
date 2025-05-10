package com.patil.govindafinance.dao.request;

import com.patil.govindafinance.customvalidator.ValidKycId;
import com.patil.govindafinance.enums.KycIdType;
import com.patil.govindafinance.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CustomerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Address address;
    private LocalDate dateOfBirth;
    private String kycIdType;
    @ValidKycId
    private String kycIdNumber;
}
