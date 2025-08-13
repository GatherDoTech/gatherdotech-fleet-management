package com.gatherdotech.fleetmanagement.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response returned for failed requests")
public class ApiError {

    @Schema(description = "Timestamp when the error occurred", example = "2025-08-13T14:28:32.123+02:00")
    private final OffsetDateTime timestamp = OffsetDateTime.now();

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error key", example = "NOT_FOUND")
    private String error;

    @Schema(description = "Human-readable error message", example = "Driver with ID 10 not found")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/api/v1/drivers/10")
    private String path;

    @Schema(description = "Application-specific error code", example = "DRIVER_NOT_FOUND")
    private String code;

    @Schema(description = "Field-level validation errors", example = "{\"email\": \"must be a valid email\"}")
    private Map<String, String> fieldErrors;

    @Schema(description = "Detailed list of messages or troubleshooting hints")
    private List<String> details;

    // --- Builder-style fluent setters ---
    public ApiError status(int v) {
        this.status = v;
        return this;
    }

    public ApiError error(String v) {
        this.error = v;
        return this;
    }

    public ApiError message(String v) {
        this.message = v;
        return this;
    }

    public ApiError path(String v) {
        this.path = v;
        return this;
    }

    public ApiError code(String v) {
        this.code = v;
        return this;
    }

    public ApiError fieldErrors(Map<String, String> v) {
        this.fieldErrors = v;
        return this;
    }

    public ApiError details(List<String> v) {
        this.details = v;
        return this;
    }
}
