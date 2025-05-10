package com.patil.govindafinance.customvalidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = KycValidator.class)  // Make sure this points to KycValidator
@Target({ElementType.FIELD})  // Apply validation on field level
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidKycId {
    String message() default "Invalid KYC ID!!";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
