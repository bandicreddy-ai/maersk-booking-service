package com.maersk.booking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OneOfIntValidator.class)
public @interface OneOfInt {
    String message() default "value must be one of the allowed integers";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int[] value();
}
