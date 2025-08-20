package com.gatherdotech.fleetmanagement.dto;

import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record DriverUpdateRequest(
        @NotBlank @Size(max = 60) String firstName,
        @NotBlank @Size(max = 60) String lastName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(max = 40) String licenseNumber,
        @NotNull @Future LocalDate licenseExpiry,
        @NotNull DriverStatus status
) {}
