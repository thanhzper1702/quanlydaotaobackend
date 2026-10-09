package com.university.modules.lecturer;

import java.util.List;

public class LecturerException extends RuntimeException {
    private final int status;
    private final List<String> errors;

    public LecturerException(int status, String message) {
        this(status, message, null);
    }

    public LecturerException(int status, String message, List<String> errors) {
        super(message);
        this.status = status;
        this.errors = errors == null ? null : List.copyOf(errors);
    }

    public int getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }
}
