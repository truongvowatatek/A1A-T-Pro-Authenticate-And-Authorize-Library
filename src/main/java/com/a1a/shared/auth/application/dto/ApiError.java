package com.a1a.shared.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.http.HttpStatus;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    public static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private HttpStatus status;

    private Long errorCode;

    private Date date;

    private String message;

    private String debugMessage;

    private ApiError() {}

    public ApiError(HttpStatus status) {
        this(status, null, null, null);
    }

    public ApiError(HttpStatus status, Long errorCode) {
        this(status, errorCode, null, null);
    }

    public ApiError(HttpStatus status, String message) {
        this(status, null, message, null);
    }

    public ApiError(HttpStatus status, Long errorCode, String message) {
        this(status, errorCode, message, null);
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this(status, null, "Unexpected error", ex);
    }

    public ApiError(HttpStatus status, Long errorCode, Throwable ex) {
        this(status, errorCode, "Unexpected error", ex);
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this(status, null, message, ex);
    }

    public ApiError(HttpStatus status, Long errorCode, String message, Throwable ex) {
        this.date = new Date();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.debugMessage = ex != null ? ex.getMessage() : null;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public Long getErrorCode() {
        return this.errorCode;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME_PATTERN, timezone = "UTC")
    public Date getDate() {
        return this.date;
    }

    public String getMessage() {
        return this.message;
    }

    public String getDebugMessage() {
        return this.debugMessage;
    }
}
