package com.university.modules.lecturer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.university.core.common.ApiResponse;
import com.university.core.util.JsonUtil;
import com.university.modules.lecturer.LectureRequest.UpdateAttendanceRequest;
import com.university.modules.lecturer.LectureRequest.UpdateGradesRequest;
import jakarta.annotation.Priority;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Priority(4000)
public class LectureRequestReader implements MessageBodyReader<Object> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isLecturerRequestType(type) && isApplicationJson(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type,
                           Type genericType,
                           Annotation[] annotations,
                           MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException {
        try {
            return JsonUtil.getMapper()
                    .readerFor(type)
                    .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                    .readValue(entityStream);
        } catch (JsonProcessingException e) {
            throw malformedRequest();
        }
    }

    private static boolean isLecturerRequestType(Class<?> type) {
        return type == UpdateGradesRequest.class || type == UpdateAttendanceRequest.class;
    }

    private static boolean isApplicationJson(MediaType mediaType) {
        return mediaType != null
                && "application".equalsIgnoreCase(mediaType.getType())
                && "json".equalsIgnoreCase(mediaType.getSubtype());
    }

    private static BadRequestException malformedRequest() {
        String body = JsonUtil.toJson(ApiResponse.error(400, "Dữ liệu JSON không hợp lệ"));
        Response response = Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(body)
                .build();
        return new BadRequestException(response);
    }
}
