package com.university.modules.assistant;

import com.university.core.common.ApiResponse;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts unexpected application failures into the public API envelope.
 * Internal exception messages are deliberately never sent to clients.
 */
@Provider
@Priority(Priorities.USER)
public class AssistantExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(AssistantExceptionMapper.class);
    private static final String SAFE_MESSAGE = "Đã xảy ra lỗi hệ thống.";

    @Override
    public Response toResponse(Exception exception) {
        log.error("Unhandled assistant API exception", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), SAFE_MESSAGE))
                .build();
    }
}
