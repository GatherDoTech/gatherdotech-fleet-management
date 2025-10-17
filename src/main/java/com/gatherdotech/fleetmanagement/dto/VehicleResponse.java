package com.gatherdotech.fleetmanagement.dto;

import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String plateNumber,
        String vin,
        String make,
        String model,
        Integer year,
        String color,
        Integer mileage,
        VehicleStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long version) {}
