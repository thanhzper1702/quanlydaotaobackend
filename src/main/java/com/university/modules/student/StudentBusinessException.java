package com.university.modules.student;

public class StudentBusinessException extends RuntimeException {

    private final int statusCode;

    public StudentBusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
