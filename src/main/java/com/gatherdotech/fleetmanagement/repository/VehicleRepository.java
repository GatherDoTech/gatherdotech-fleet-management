package com.gatherdotech.fleetmanagement.repository;

import com.gatherdotech.fleetmanagement.entity.Vehicle;
import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    boolean existsByPlateNumber(String plateNumber);

    boolean existsByVin(String vin);

    Page<Vehicle> findAllByStatus(VehicleStatus status, Pageable pageable);
}
