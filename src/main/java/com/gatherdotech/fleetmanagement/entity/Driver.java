package com.gatherdotech.fleetmanagement.entity;

import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "drivers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_drivers_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_drivers_license_number", columnNames = "license_number")
        })
public class Driver {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotBlank @Size(max = 60)
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @NotBlank @Size(max = 60)
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @NotBlank @Email @Size(max = 120)
    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @NotBlank @Size(max = 30)
    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @NotBlank @Size(max = 40)
    @Column(name = "license_number", nullable = false, length = 40)
    private String licenseNumber;

    @Future @Column(name = "license_expiry", nullable = false)
    private LocalDate licenseExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private DriverStatus status = DriverStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Version
    private long version;

    @PreUpdate
    void onUpdate() { this.updatedAt = OffsetDateTime.now(); }

    // getters/setters/constructors omitted for brevity
    // Lombok tip: @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
}
