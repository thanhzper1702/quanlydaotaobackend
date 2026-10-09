package com.university.modules.notification;

public class NotificationBusinessException extends RuntimeException {

    private final int statusCode;

    public NotificationBusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
