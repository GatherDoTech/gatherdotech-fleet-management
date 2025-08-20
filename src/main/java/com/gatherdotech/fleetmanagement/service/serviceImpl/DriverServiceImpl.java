package com.gatherdotech.fleetmanagement.service.serviceImpl;

import com.gatherdotech.fleetmanagement.dto.DriverRequest;
import com.gatherdotech.fleetmanagement.dto.DriverResponse;
import com.gatherdotech.fleetmanagement.dto.DriverUpdateRequest;
import com.gatherdotech.fleetmanagement.entity.Driver;
import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import com.gatherdotech.fleetmanagement.exception.ConflictException;
import com.gatherdotech.fleetmanagement.exception.NotFoundException;
import com.gatherdotech.fleetmanagement.repository.DriverRepository;
import com.gatherdotech.fleetmanagement.service.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository repo;

    public DriverServiceImpl(DriverRepository repo) {
        this.repo = repo;
    }

    @Override
    public DriverResponse create(DriverRequest req) {
        if (repo.existsByEmail(req.email())) {
            throw new ConflictException("DRIVER_EMAIL_EXISTS", "Email already in use");
        }
        if (repo.existsByLicenseNumber(req.licenseNumber())) {
            throw new ConflictException("DRIVER_LICENSE_EXISTS", "License number already in use");
        }

        Driver d = new Driver();
        d.setFirstName(req.firstName());
        d.setLastName(req.lastName());
        d.setEmail(req.email());
        d.setPhone(req.phone());
        d.setLicenseNumber(req.licenseNumber());
        d.setLicenseExpiry(req.licenseExpiry());
        d.setStatus(req.status() == null ? DriverStatus.ACTIVE : req.status());

        d = repo.save(d);
        return toResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponse get(UUID id) {
        Driver d = repo.findById(id).orElseThrow(() ->
                new NotFoundException("DRIVER_NOT_FOUND", "Driver not found: " + id));
        return toResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponse> list(DriverStatus status, Pageable pageable) {
        return (status == null ? repo.findAll(pageable) : repo.findAllByStatus(status, pageable))
                .map(this::toResponse);
    }

    @Override
    public DriverResponse update(UUID id, DriverUpdateRequest req) {
        Driver d = repo.findById(id).orElseThrow(() ->
                new NotFoundException("DRIVER_NOT_FOUND", "Driver not found: " + id));

        // Uniqueness checks only when values change
        if (!d.getEmail().equals(req.email()) && repo.existsByEmail(req.email())) {
            throw new ConflictException("DRIVER_EMAIL_EXISTS", "Email already in use");
        }
        if (!d.getLicenseNumber().equals(req.licenseNumber()) && repo.existsByLicenseNumber(req.licenseNumber())) {
            throw new ConflictException("DRIVER_LICENSE_EXISTS", "License number already in use");
        }

        d.setFirstName(req.firstName());
        d.setLastName(req.lastName());
        d.setEmail(req.email());
        d.setPhone(req.phone());
        d.setLicenseNumber(req.licenseNumber());
        d.setLicenseExpiry(req.licenseExpiry());
        d.setStatus(req.status());

        return toResponse(d);
    }

    @Override
    public void deactivate(UUID id) {
        Driver d = repo.findById(id).orElseThrow(() ->
                new NotFoundException("DRIVER_NOT_FOUND", "Driver not found: " + id));
        d.setStatus(DriverStatus.INACTIVE);
    }

    private DriverResponse toResponse(Driver d) {
        return new DriverResponse(
                d.getId(), d.getFirstName(), d.getLastName(), d.getEmail(), d.getPhone(),
                d.getLicenseNumber(), d.getLicenseExpiry(), d.getStatus(),
                d.getCreatedAt(), d.getUpdatedAt(), d.getVersion()
        );
    }
}
