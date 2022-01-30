package com.challenge1.item.validations.validators;

import com.challenge1.item.validations.annotations.Identifier;
import com.google.common.base.Strings;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentifierValidator extends AbstractEmptyValidator<Identifier> {

    private static final String PATTERN = "[0-9]+";
    /*We will use this max length since this is the largest number of chars our string with id can/should have if there is more
      we will need to evaluate new data types for the database
     */
    private static final int MAX_LENGTH = 19;

    @Override
    public void initialize(Identifier identifier) {
        this.setNullable(identifier.nullable());
        this.setEmpty(identifier.empty());
    }

    @Override
    protected boolean doIsValid(String value, ConstraintValidatorContext context) {
        boolean valid;

        if (Strings.isNullOrEmpty(value)) {
            valid = true;
        } else if (value.length() > MAX_LENGTH) {
            valid = false;
        } else {
            Pattern pattern = Pattern.compile(PATTERN);
            Matcher matcher = pattern.matcher(value);
            valid = matcher.matches();
        }

        return valid;
    }
}
