package com.university.modules.profile.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePasswordResponseDTO {

    @JsonProperty("message")
    private String message;

    public ChangePasswordResponseDTO() {
    }

    public ChangePasswordResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
