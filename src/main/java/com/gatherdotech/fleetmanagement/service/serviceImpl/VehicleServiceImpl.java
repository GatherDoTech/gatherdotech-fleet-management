package com.gatherdotech.fleetmanagement.service.serviceImpl;

import com.gatherdotech.fleetmanagement.dto.VehicleRequest;
import com.gatherdotech.fleetmanagement.dto.VehicleResponse;
import com.gatherdotech.fleetmanagement.dto.VehicleUpdateRequest;
import com.gatherdotech.fleetmanagement.entity.Vehicle;
import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import com.gatherdotech.fleetmanagement.exception.ConflictException;
import com.gatherdotech.fleetmanagement.exception.NotFoundException;
import com.gatherdotech.fleetmanagement.repository.VehicleRepository;
import com.gatherdotech.fleetmanagement.service.VehicleService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository repository) {
        this.vehicleRepository = repository;
    }

    @Override
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByPlateNumber(request.plateNumber())) {
            throw new ConflictException(
                    "VEHICLE_PLATE_EXISTS", "Plate number already in use");
        }
        if (vehicleRepository.existsByVin(request.vin())) {
            throw new ConflictException("VEHICLE_VIN_EXISTS", "VIN already in use");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(request.plateNumber());
        vehicle.setVin(request.vin());
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setYear(request.year());
        vehicle.setColor(request.color());
        vehicle.setMileage(request.mileage());
        vehicle.setStatus(request.status() == null ? VehicleStatus.AVAILABLE : request.status());

        vehicle = vehicleRepository.save(vehicle);
        return toResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse get(UUID id) {
        Vehicle vehicle =
                vehicleRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "VEHICLE_NOT_FOUND",
                                                "Vehicle not found: " + id));
        return toResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponse> list(VehicleStatus status, Pageable pageable) {
        return (status == null
                        ?vehicleRepository.findAll(pageable)
                        : vehicleRepository.findAllByStatus(status, pageable))
                .map(this::toResponse);
    }

    @Override
    public VehicleResponse update(UUID id, VehicleUpdateRequest request) {
        Vehicle vehicle =
                vehicleRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "VEHICLE_NOT_FOUND",
                                                "Vehicle not found: " + id));

        // Uniqueness checks only when values change
        if (!vehicle.getPlateNumber().equals( request.plateNumber())
                && vehicleRepository.existsByPlateNumber( request.plateNumber())) {
            throw new ConflictException(
                    "VEHICLE_PLATE_EXISTS", "Plate number already in use");
        }
        if (!vehicle.getVin().equals( request.vin()) && vehicleRepository.existsByVin( request.vin())) {
            throw new ConflictException("VEHICLE_VIN_EXISTS", "VIN already in use");
        }

        vehicle.setPlateNumber( request.plateNumber());
        vehicle.setVin( request.vin());
        vehicle.setMake( request.make());
        vehicle.setModel( request.model());
        vehicle.setYear( request.year());
        vehicle.setColor( request.color());
        vehicle.setMileage( request.mileage());
        vehicle.setStatus( request.status());

        return toResponse(vehicle);
    }

    @Override
    public void delete(UUID id) {
        Vehicle vehicle =
                vehicleRepository.findById(id)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "VEHICLE_NOT_FOUND",
                                                "Vehicle not found: " + id));
        vehicleRepository.delete(vehicle);
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getVin(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getColor(),
                vehicle.getMileage(),
                vehicle.getStatus(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt(),
                vehicle.getVersion());
    }
}
