package com.maersk.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneOfIntValidator implements ConstraintValidator<OneOfInt, Integer> {
    private static final Logger log = LoggerFactory.getLogger(OneOfIntValidator.class);
    private int[] allowed;

    @Override
    public void initialize(OneOfInt constraintAnnotation) {
        this.allowed = constraintAnnotation.value();
        log.debug("Initialized OneOfInt with values {}", (Object) allowed);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return false;
        for (int v : allowed) if (v == value) return true;
        return false;
    }
}
