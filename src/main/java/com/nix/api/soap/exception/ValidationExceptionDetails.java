package com.nix.api.soap.exception;

public class ValidationExceptionDetails {

    private String invalidField;
    private String validationMessage;

    public ValidationExceptionDetails(String invalidField, String validationMessage) {
        this.invalidField = invalidField;
        this.validationMessage = validationMessage;
    }

    public String getInvalidField() {
        return invalidField;
    }

    public void setInvalidField(String invalidField) {
        this.invalidField = invalidField;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
}