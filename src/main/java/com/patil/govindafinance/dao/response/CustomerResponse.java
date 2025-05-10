package com.patil.govindafinance.dao.response;

import com.patil.govindafinance.customvalidator.ValidKycId;
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
public class CustomerResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
    private Address address;
    private LocalDate dateOfBirth;
    private String kycIdType;
    private String kycIdNumber;
}
