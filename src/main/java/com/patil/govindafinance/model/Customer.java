package com.patil.govindafinance.model;

import com.patil.govindafinance.customvalidator.ValidKycId;
import com.patil.govindafinance.enums.KycIdType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    private String id;

    @NotBlank(message = "Full Name cannot be blank")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    private String phoneNumber;

    private Address address;

    private String password;

    private LocalDate dateOfBirth;

    private KycIdType kycIdType;

    @ValidKycId
    @Indexed(unique = true)
    private String kycIdNumber;

    private LocalDate kycVarifiedOn;

    private LocalDate registeredOn;

}
