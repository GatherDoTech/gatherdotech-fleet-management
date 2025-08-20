package com.gatherdotech.fleetmanagement.repository;

import com.gatherdotech.fleetmanagement.entity.Driver;
import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    boolean existsByEmail(String email);
    boolean existsByLicenseNumber(String licenseNumber);
    Optional<Driver> findByEmail(String email);
    Page<Driver> findAllByStatus(DriverStatus status, Pageable pageable);
}