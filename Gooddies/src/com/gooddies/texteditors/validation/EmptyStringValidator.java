package com.gooddies.texteditors.validation;

/**
 * @author Dmitry
 */
public class EmptyStringValidator extends AbstractValidator {

    public EmptyStringValidator(String text) {
        setErrorMessage(text);
    }

    @Override
    public boolean validate(String text) {
        return !(text == null || text.trim().isEmpty());
    }
}
