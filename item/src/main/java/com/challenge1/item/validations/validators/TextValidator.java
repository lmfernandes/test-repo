package com.challenge1.item.validations.validators;


import com.challenge1.item.validations.annotations.Text;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidator extends AbstractEmptyValidator<Text> {

    private static final String PATTERN = "[\\p{IsAlphabetic}\\p{IsDigit}\\u00C0-\\u017F\\s]+";

    @Override
    public void initialize(Text text) {
        this.setNullable(text.nullable());
        this.setEmpty(text.empty());
    }

    @Override
    protected boolean doIsValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
