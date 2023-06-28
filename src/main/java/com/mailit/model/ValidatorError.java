package com.mailit.model;

public enum ValidatorError {
    INVALID_REPLY_TO_EMAIL("Reply to email is not valid"),
    INVALID_DESTINATION_EMAIL("Destination email(s) are not valid"),
    MESSAGE_REQUIRED("Message is required"),
    REPLY_TO_REQUIRED("ReplyTo is required");

    private final String errorMessage;

    ValidatorError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.name() + ": " + errorMessage;
    }
}
