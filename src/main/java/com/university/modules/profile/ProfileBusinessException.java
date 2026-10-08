package com.university.modules.profile;

public class ProfileBusinessException extends RuntimeException {

    private final int statusCode;

    public ProfileBusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
