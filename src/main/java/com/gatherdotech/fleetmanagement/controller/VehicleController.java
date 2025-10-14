package com.gatherdotech.fleetmanagement.controller;

import com.gatherdotech.fleetmanagement.dto.VehicleRequest;
import com.gatherdotech.fleetmanagement.dto.VehicleResponse;
import com.gatherdotech.fleetmanagement.dto.VehicleUpdateRequest;
import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import com.gatherdotech.fleetmanagement.service.VehicleService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    /** Create a vehicle */
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse created = service.create(request);
        // Location: /api/v1/vehicles/{id}
        URI location =
                URI.create(String.format("/api/v1/vehicles/%s", created.id()));
        return ResponseEntity.created(location).body(created);
    }

    /** Get a single vehicle by ID */
    @GetMapping("/{id}")
    public VehicleResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    /** List vehicles with optional status filter + pagination */
    @GetMapping
    public Page<VehicleResponse> list(
            @RequestParam(required = false) VehicleStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return service.list(status, pageable);
    }

    /** Full update (idempotent) */
    @PutMapping("/{id}")
    public VehicleResponse update(
            @PathVariable UUID id, @Valid @RequestBody VehicleUpdateRequest request) {
        return service.update(id, request);
    }

    /** Hard delete vehicle */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
