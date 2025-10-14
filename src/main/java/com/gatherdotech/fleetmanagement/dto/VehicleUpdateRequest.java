package com.gatherdotech.fleetmanagement.dto;

import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleUpdateRequest(
        @NotBlank(message = "Plate number is required")
                @Size(max = 20, message = "Plate number must not exceed 20 characters")
                String plateNumber,
        @NotBlank(message = "VIN is required")
                @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
                String vin,
        @NotBlank(message = "Make is required")
                @Size(max = 50, message = "Make must not exceed 50 characters")
                String make,
        @NotBlank(message = "Model is required")
                @Size(max = 50, message = "Model must not exceed 50 characters")
                String model,
        @NotNull(message = "Year is required")
                @Min(value = 1900, message = "Year must be 1900 or later")
                Integer year,
        @Size(max = 30, message = "Color must not exceed 30 characters") String color,
        @Min(value = 0, message = "Mileage cannot be negative") Integer mileage,
        @NotNull(message = "Status is required") VehicleStatus status) {}
