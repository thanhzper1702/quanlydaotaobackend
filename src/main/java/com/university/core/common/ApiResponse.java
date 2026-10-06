package com.university.core.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private Boolean success;
    private String message;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private T data;

    private Object meta;
    private List<String> errors;
    private LocalDateTime timestamp;

    public ApiResponse() {
    }

    public ApiResponse(int status, boolean success, String message, T data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int status, boolean success, String message, T data, List<String> errors) {
        this();
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, true, message, data);
    }

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(status, true, message, data);
    }

    public static <T> ApiResponse<T> success(int status, String message, T data, Object meta) {
        ApiResponse<T> resp = new ApiResponse<>(status, true, message, data);
        resp.setMeta(meta);
        return resp;
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, false, message, null);
    }

    public static <T> ApiResponse<T> error(int status, String message, List<String> errors) {
        return new ApiResponse<>(status, false, message, null, errors);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(400, false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(400, false, message, null, errors);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
