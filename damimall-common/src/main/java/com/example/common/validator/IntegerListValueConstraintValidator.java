package com.example.common.validator;

import com.example.common.valid.IntegerListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class IntegerListValueConstraintValidator implements ConstraintValidator<IntegerListValue, Integer> {
    private Set<Integer> vals = new HashSet<>();

    @Override
    public void initialize(IntegerListValue constraintAnnotation) {
        for (int i : constraintAnnotation.vals()){
            vals.add(i);
        }
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return vals.contains(integer);
    }
}
