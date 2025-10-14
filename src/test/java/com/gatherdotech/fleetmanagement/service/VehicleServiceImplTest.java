package com.gatherdotech.fleetmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gatherdotech.fleetmanagement.dto.VehicleRequest;
import com.gatherdotech.fleetmanagement.dto.VehicleResponse;
import com.gatherdotech.fleetmanagement.dto.VehicleUpdateRequest;
import com.gatherdotech.fleetmanagement.entity.Vehicle;
import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import com.gatherdotech.fleetmanagement.exception.ConflictException;
import com.gatherdotech.fleetmanagement.exception.NotFoundException;
import com.gatherdotech.fleetmanagement.repository.VehicleRepository;
import com.gatherdotech.fleetmanagement.service.serviceImpl.VehicleServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

class VehicleServiceImplTest {

    private VehicleRepository vehicleRepository;
    private VehicleService service;

    @BeforeEach
    void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
        service = new VehicleServiceImpl( vehicleRepository);
    }

    @Test
    void create_success() {
        VehicleRequest request =
                new VehicleRequest(
                        "ABC-123-GP",
                        "1HGBH41JXMN109186",
                        "Toyota",
                        "Hilux",
                        2022,
                        "White",
                        50000,
                        VehicleStatus.AVAILABLE);

        when( vehicleRepository.existsByPlateNumber("ABC-123-GP")).thenReturn(false);
        when( vehicleRepository.existsByVin("1HGBH41JXMN109186")).thenReturn(false);

        Vehicle saved = new Vehicle();
        saved.setId(UUID.randomUUID());
        saved.setPlateNumber("ABC-123-GP");
        saved.setVin("1HGBH41JXMN109186");
        saved.setMake("Toyota");
        saved.setModel("Hilux");
        saved.setYear(2022);
        saved.setColor("White");
        saved.setMileage(50000);
        saved.setStatus(VehicleStatus.AVAILABLE);

        when( vehicleRepository.save(any(Vehicle.class))).thenReturn(saved);

        VehicleResponse res = service.create(request);

        assertNotNull(res);
        assertEquals("ABC-123-GP", res.plateNumber());
        assertEquals("Toyota", res.make());
        assertEquals("Hilux", res.model());
        verify( vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void create_duplicatePlateNumber_conflict() {
        VehicleRequest request =
                new VehicleRequest(
                        "DUP-999",
                        "1HGBH41JXMN109186",
                        "Ford",
                        "Ranger",
                        2021,
                        "Red",
                        20000,
                        null);
        when( vehicleRepository.existsByPlateNumber("DUP-999")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request));
    }

    @Test
    void create_duplicateVin_conflict() {
        VehicleRequest request =
                new VehicleRequest(
                        "XYZ-888",
                        "DUPVIN12345678901",
                        "Nissan",
                        "Navara",
                        2020,
                        "Blue",
                        30000,
                        null);
        when( vehicleRepository.existsByPlateNumber("XYZ-888")).thenReturn(false);
        when( vehicleRepository.existsByVin("DUPVIN12345678901")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(request));
    }

    @Test
    void get_success() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setPlateNumber("TEST-123");
        vehicle.setVin("1HGBH41JXMN109186");
        vehicle.setMake("Honda");
        vehicle.setModel("Civic");
        vehicle.setYear(2019);
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        when( vehicleRepository.findById(id)).thenReturn(Optional.of(vehicle));

        VehicleResponse res = service.get(id);

        assertNotNull(res);
        assertEquals(id, res.id());
        assertEquals("TEST-123", res.plateNumber());
    }

    @Test
    void get_notFound_throws() {
        UUID id = UUID.randomUUID();
        when( vehicleRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(id));
    }

    @Test
    void list_withStatus_filtersCorrectly() {
        Vehicle v1 = createVehicle(UUID.randomUUID(), "ABC-123", VehicleStatus.AVAILABLE);
        Vehicle v2 = createVehicle(UUID.randomUUID(), "DEF-456", VehicleStatus.AVAILABLE);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(v1, v2), pageable, 2);

        when( vehicleRepository.findAllByStatus(VehicleStatus.AVAILABLE, pageable)).thenReturn(vehiclePage);

        Page<VehicleResponse> result = service.list(VehicleStatus.AVAILABLE, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("ABC-123", result.getContent().get(0).plateNumber());
    }

    @Test
    void list_withoutStatus_returnsAll() {
        Vehicle v1 = createVehicle(UUID.randomUUID(), "ABC-123", VehicleStatus.AVAILABLE);
        Vehicle v2 = createVehicle(UUID.randomUUID(), "DEF-456", VehicleStatus.IN_USE);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> vehiclePage = new PageImpl<>(List.of(v1, v2), pageable, 2);

        when( vehicleRepository.findAll(pageable)).thenReturn(vehiclePage);

        Page<VehicleResponse> result = service.list(null, pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void update_success() {
        UUID id = UUID.randomUUID();
        Vehicle existing = new Vehicle();
        existing.setId(id);
        existing.setPlateNumber("OLD-123");
        existing.setVin("OLDVIN1234567890A");
        existing.setMake("Toyota");
        existing.setModel("Corolla");
        existing.setYear(2018);
        existing.setStatus(VehicleStatus.AVAILABLE);

        when( vehicleRepository.findById(id)).thenReturn(Optional.of(existing));
        when( vehicleRepository.existsByPlateNumber("NEW-456")).thenReturn(false);
        when( vehicleRepository.existsByVin("NEWVIN1234567890B")).thenReturn(false);

        VehicleUpdateRequest updateReq =
                new VehicleUpdateRequest(
                        "NEW-456",
                        "NEWVIN1234567890B",
                        "Toyota",
                        "Corolla",
                        2018,
                        "Silver",
                        60000,
                        VehicleStatus.IN_USE);

        VehicleResponse res = service.update(id, updateReq);

        assertEquals("NEW-456", res.plateNumber());
        assertEquals(VehicleStatus.IN_USE, res.status());
    }

    @Test
    void update_duplicatePlateNumber_conflict() {
        UUID id = UUID.randomUUID();
        Vehicle existing = new Vehicle();
        existing.setId(id);
        existing.setPlateNumber("OLD-123");
        existing.setVin("VIN12345678901234");

        when( vehicleRepository.findById(id)).thenReturn(Optional.of(existing));
        when( vehicleRepository.existsByPlateNumber("DUP-999")).thenReturn(true);

        VehicleUpdateRequest u =
                new VehicleUpdateRequest(
                        "DUP-999", // duplicate plate
                        "VIN12345678901234",
                        "Toyota",
                        "Hilux",
                        2022,
                        "White",
                        10000,
                        VehicleStatus.AVAILABLE);

        assertThrows(ConflictException.class, () -> service.update(id, u));
    }

    @Test
    void update_duplicateVin_conflict() {
        UUID id = UUID.randomUUID();
        Vehicle existing = new Vehicle();
        existing.setId(id);
        existing.setPlateNumber("ABC-123");
        existing.setVin("OLDVIN1234567890A");

        when( vehicleRepository.findById(id)).thenReturn(Optional.of(existing));
        when( vehicleRepository.existsByPlateNumber("ABC-123")).thenReturn(false);
        when( vehicleRepository.existsByVin("DUPVIN1234567890B")).thenReturn(true);

        VehicleUpdateRequest request =
                new VehicleUpdateRequest(
                        "ABC-123",
                        "DUPVIN1234567890B", // duplicate VIN
                        "Ford",
                        "Ranger",
                        2021,
                        "Black",
                        5000,
                        VehicleStatus.AVAILABLE);

        assertThrows(ConflictException.class, () -> service.update(id, request));
    }

    @Test
    void update_notFound_throws() {
        UUID id = UUID.randomUUID();
        when( vehicleRepository.findById(id)).thenReturn(Optional.empty());

        VehicleUpdateRequest request =
                new VehicleUpdateRequest(
                        "ABC-123",
                        "VIN12345678901234",
                        "Toyota",
                        "Hilux",
                        2022,
                        "White",
                        10000,
                        VehicleStatus.AVAILABLE);

        assertThrows(NotFoundException.class, () -> service.update(id, request));
    }

    @Test
    void delete_success() {
        UUID id = UUID.randomUUID();
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setPlateNumber("DEL-123");
        vehicle.setVin("VIN12345678901234");
        vehicle.setMake("Toyota");
        vehicle.setModel("Hilux");
        vehicle.setYear(2020);
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        when( vehicleRepository.findById(id)).thenReturn(Optional.of(vehicle));

        service.delete(id);

        verify( vehicleRepository).delete(vehicle);
    }

    @Test
    void delete_notFound_throws() {
        UUID id = UUID.randomUUID();
        when( vehicleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.delete(id));
    }

    private Vehicle createVehicle(UUID id, String plateNumber, VehicleStatus status) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setPlateNumber(plateNumber);
        vehicle.setVin("1HGBH41JXMN109186");
        vehicle.setMake("Toyota");
        vehicle.setModel("Hilux");
        vehicle.setYear(2022);
        vehicle.setStatus(status);
        return vehicle;
    }
}
