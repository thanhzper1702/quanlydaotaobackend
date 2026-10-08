package com.university.modules.notification.responseDTO;

public class DeleteNotificationResponseDTO {

    private boolean success;
    private String message;

    public DeleteNotificationResponseDTO() {
    }

    public DeleteNotificationResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
