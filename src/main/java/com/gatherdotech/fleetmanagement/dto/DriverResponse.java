package com.gatherdotech.fleetmanagement.dto;

import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DriverResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String licenseNumber,
        LocalDate licenseExpiry,
        DriverStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long version
) {}

