package com.challenge1.item.validations.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractEmptyValidator<A extends Annotation> implements ConstraintValidator<A, String> {

    private boolean nullable;
    private boolean empty;

    protected boolean isNullable() {
        return this.nullable;
    }

    protected void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    protected abstract boolean doIsValid(String value, ConstraintValidatorContext context);

    public boolean isValidRegexp(String patternRegexp, String value) {
        Pattern pattern = Pattern.compile(patternRegexp);
        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }

    @Override
    public final boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return this.isNullable();
        } else if (value.isEmpty()) {
            return this.isEmpty();
        }

        return this.doIsValid(value, context);
    }
}
