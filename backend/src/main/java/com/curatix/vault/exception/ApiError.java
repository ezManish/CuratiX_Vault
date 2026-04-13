package com.curatix.vault.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private List<ValidationError> subErrors;

    public ApiError() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Static builder-like method to avoid breaking GlobalExceptionHandler logic
    public static ApiErrorBuilder builder() {
        return new ApiErrorBuilder();
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public List<ValidationError> getSubErrors() { return subErrors; }
    public void setSubErrors(List<ValidationError> subErrors) { this.subErrors = subErrors; }

    public static class ApiErrorBuilder {
        private ApiError instance = new ApiError();

        public ApiErrorBuilder status(int status) { instance.setStatus(status); return this; }
        public ApiErrorBuilder error(String error) { instance.setError(error); return this; }
        public ApiErrorBuilder message(String message) { instance.setMessage(message); return this; }
        public ApiErrorBuilder path(String path) { instance.setPath(path); return this; }
        public ApiErrorBuilder subErrors(List<ValidationError> subErrors) { instance.setSubErrors(subErrors); return this; }
        public ApiError build() { return instance; }
    }

    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;

        public ValidationError() {}
        public ValidationError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public static ValidationErrorBuilder builder() {
            return new ValidationErrorBuilder();
        }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getRejectedValue() { return rejectedValue; }
        public void setRejectedValue(Object rejectedValue) { this.rejectedValue = rejectedValue; }

        public static class ValidationErrorBuilder {
            private ValidationError instance = new ValidationError();
            public ValidationErrorBuilder field(String field) { instance.setField(field); return this; }
            public ValidationErrorBuilder message(String message) { instance.setMessage(message); return this; }
            public ValidationErrorBuilder rejectedValue(Object rejectedValue) { instance.setRejectedValue(rejectedValue); return this; }
            public ValidationError build() { return instance; }
        }
    }
}
