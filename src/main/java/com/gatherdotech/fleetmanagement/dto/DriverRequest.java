package com.gatherdotech.fleetmanagement.dto;

import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record DriverRequest(
        @NotBlank @Size(max = 60) String firstName,
        @NotBlank @Size(max = 60) String lastName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(max = 40) String licenseNumber,
        @NotNull @Future LocalDate licenseExpiry,
        DriverStatus status // optional; defaults to ACTIVE in service if null
) {}
