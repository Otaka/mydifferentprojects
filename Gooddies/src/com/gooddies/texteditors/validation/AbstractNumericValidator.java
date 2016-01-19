package com.gooddies.texteditors.validation;

/**
 * @author Dmitry
 */
public abstract class AbstractNumericValidator extends AbstractValidator
{
    @Override
    public boolean validate(String text)
    {
        throw new UnsupportedOperationException("Method validate(String) is not implemented in AbstractNumberValidator. use validate(double)");
    }

    public abstract boolean validate(double number);
}
