package com.university.modules.auth;

public class AuthResult<T> {

    private final boolean success;
    private final int statusCode;
    private final String message;
    private final T data;
    private final String refreshToken;

    public AuthResult(boolean success, int statusCode, String message, T data, String refreshToken) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.refreshToken = refreshToken;
    }

    public static <T> AuthResult<T> success(T data, String refreshToken, String message) {
        return new AuthResult<>(true, 200, message, data, refreshToken);
    }

    public static <T> AuthResult<T> error(int statusCode, String message) {
        return new AuthResult<>(false, statusCode, message, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
