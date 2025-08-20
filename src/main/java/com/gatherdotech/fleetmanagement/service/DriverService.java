package com.gatherdotech.fleetmanagement.service;

import com.gatherdotech.fleetmanagement.dto.DriverRequest;
import com.gatherdotech.fleetmanagement.dto.DriverResponse;
import com.gatherdotech.fleetmanagement.dto.DriverUpdateRequest;
import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DriverService {
    DriverResponse create(DriverRequest request);
    DriverResponse get(UUID id);
    Page<DriverResponse> list(DriverStatus status, Pageable pageable);
    DriverResponse update(UUID id, DriverUpdateRequest request);
    void deactivate(UUID id); // or delete if you prefer hard deletes
}
