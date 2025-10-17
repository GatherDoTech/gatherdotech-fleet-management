package com.gatherdotech.fleetmanagement.service;

import com.gatherdotech.fleetmanagement.dto.VehicleRequest;
import com.gatherdotech.fleetmanagement.dto.VehicleResponse;
import com.gatherdotech.fleetmanagement.dto.VehicleUpdateRequest;
import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    VehicleResponse create(VehicleRequest request);

    VehicleResponse get(UUID id);

    Page<VehicleResponse> list(VehicleStatus status, Pageable pageable);

    VehicleResponse update(UUID id, VehicleUpdateRequest request);

    void delete(UUID id);
}
