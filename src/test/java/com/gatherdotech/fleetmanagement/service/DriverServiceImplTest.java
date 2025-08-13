package com.gatherdotech.fleetmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.gatherdotech.fleetmanagement.dto.DriverRequest;
import com.gatherdotech.fleetmanagement.dto.DriverUpdateRequest;
import com.gatherdotech.fleetmanagement.entity.Driver;
import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import com.gatherdotech.fleetmanagement.exception.ConflictException;
import com.gatherdotech.fleetmanagement.exception.NotFoundException;
import com.gatherdotech.fleetmanagement.repository.DriverRepository;
import com.gatherdotech.fleetmanagement.service.serviceImpl.DriverServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DriverServiceImplTest {

    private DriverRepository repo;
    private DriverService service;

    @BeforeEach
    void setUp() {
        repo = mock(DriverRepository.class);
        service = new DriverServiceImpl(repo);
    }

    @Test
    void create_success() {
        DriverRequest req =
                new DriverRequest(
                        "Lindiwe",
                        "Nkosi",
                        "lindiwe@example.com",
                        "+27-82",
                        "LIC-1",
                        LocalDate.now().plusYears(1),
                        DriverStatus.ACTIVE);

        when(repo.existsByEmail("lindiwe@example.com")).thenReturn(false);
        when(repo.existsByLicenseNumber("LIC-1")).thenReturn(false);

        Driver saved = new Driver();
        saved.setId(UUID.randomUUID());
        saved.setFirstName("Lindiwe");
        saved.setLastName("Nkosi");
        saved.setEmail("lindiwe@example.com");
        saved.setPhone("+27-82");
        saved.setLicenseNumber("LIC-1");
        saved.setLicenseExpiry(req.licenseExpiry());
        saved.setStatus(DriverStatus.ACTIVE);

        when(repo.save(any(Driver.class))).thenReturn(saved);

        var res = service.create(req);
        assertEquals("lindiwe@example.com", res.email());
        verify(repo).save(any(Driver.class));
    }

    @Test
    void create_duplicateEmail_conflict() {
        DriverRequest req =
                new DriverRequest(
                        "A", "B", "dup@example.com", "+27", "LIC", LocalDate.now().plusYears(1), null);
        when(repo.existsByEmail("dup@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(req));
    }

    @Test
    void get_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(id));
    }

    @Test
    void update_duplicateLicense_conflict() {
        UUID id = UUID.randomUUID();
        Driver existing = new Driver();
        existing.setId(id);
        existing.setEmail("a@example.com");
        existing.setLicenseNumber("LIC-1");

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.existsByLicenseNumber("LIC-2")).thenReturn(true);

        DriverUpdateRequest u =
                new DriverUpdateRequest(
                        "A",
                        "B",
                        "a@example.com",
                        "+27",
                        "LIC-2", // new but already in use
                        LocalDate.now().plusYears(1),
                        DriverStatus.ACTIVE);

        assertThrows(ConflictException.class, () -> service.update(id, u));
    }
}
