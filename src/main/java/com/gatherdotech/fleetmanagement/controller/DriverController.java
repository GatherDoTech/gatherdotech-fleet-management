package com.gatherdotech.fleetmanagement.controller;

import com.gatherdotech.fleetmanagement.dto.DriverRequest;
import com.gatherdotech.fleetmanagement.dto.DriverResponse;
import com.gatherdotech.fleetmanagement.dto.DriverUpdateRequest;
import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import com.gatherdotech.fleetmanagement.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService service;

    public DriverController(DriverService service) {
        this.service = service;
    }

    /** Create a driver */
    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest req) {
        DriverResponse created = service.create(req);
        // Location: /api/v1/drivers/{id}
        URI location = URI.create(String.format("/api/v1/drivers/%s", created.id()));
        return ResponseEntity.created(location).body(created);
    }

    /** Get a single driver by ID */
    @GetMapping("/{id}")
    public DriverResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    /** List drivers with optional status filter + pagination */
    @GetMapping
    public Page<DriverResponse> list(
            @RequestParam(required = false) DriverStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return service.list(status, pageable);
    }

    /** Full update (idempotent) */
    @PutMapping("/{id}")
    public DriverResponse update(@PathVariable UUID id,
                                 @Valid @RequestBody DriverUpdateRequest req) {
        return service.update(id, req);
    }

    /** Soft-delete / deactivate driver */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) {
        service.deactivate(id);
    }
}
