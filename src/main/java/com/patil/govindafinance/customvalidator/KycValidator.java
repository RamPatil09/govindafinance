package com.patil.govindafinance.customvalidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class KycValidator implements ConstraintValidator<ValidKycId, String> {  // Change to String

    @Override
    public boolean isValid(String kycId, ConstraintValidatorContext context) {
        if (kycId == null) return true;  // Allow null values (optional)

        // Validation logic for Aadhaar or PAN
        boolean valid = false;
        String message = "Invalid KYC ID format";

        if (kycId.length() == 12) {  // AADHAAR logic
            valid = kycId.matches("\\d{12}");
            message = "Aadhaar number must be exactly 12 digits";
        } else if (kycId.length() == 10) {  // PAN logic
            valid = kycId.matches("[A-Z]{5}[0-9]{4}[A-Z]");
            message = "PAN number must be in format: e.g. ABCDE1234F";
        } else {
            valid = false;
            message = "Unsupported KYC Document ID type or incorrect Document Id";
        }

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("kycIdNumber")
                    .addConstraintViolation();
        }

        return valid;
    }
}
