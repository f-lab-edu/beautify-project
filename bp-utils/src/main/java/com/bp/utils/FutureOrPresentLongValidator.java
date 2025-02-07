package com.bp.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FutureOrPresentLongValidator implements ConstraintValidator<FutureOrPresentLong, Long> {

    @Override
    public boolean isValid(final Long value, final ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return value > System.currentTimeMillis();
    }
}
