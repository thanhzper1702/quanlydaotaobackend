package com.university.core.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.university.core.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * High-performance JSON and HTTP Response utility using Jackson.
 */
public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonUtil() {
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing to JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    public static <T> T readJson(HttpServletRequest req, Class<T> clazz) throws IOException {
        return MAPPER.readValue(req.getInputStream(), clazz);
    }

    public static void sendResponse(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        MAPPER.writeValue(resp.getWriter(), body);
    }

    public static <T> void sendSuccess(HttpServletResponse resp, String message, T data) throws IOException {
        sendResponse(resp, HttpServletResponse.SC_OK, ApiResponse.success(message, data));
    }

    public static <T> void sendSuccess(HttpServletResponse resp, int status, String message, T data) throws IOException {
        sendResponse(resp, status, ApiResponse.success(message, data));
    }

    public static void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        sendResponse(resp, status, ApiResponse.error(message));
    }

    public static void sendError(HttpServletResponse resp, int status, String message, List<String> errors) throws IOException {
        sendResponse(resp, status, ApiResponse.error(message, errors));
    }
}
