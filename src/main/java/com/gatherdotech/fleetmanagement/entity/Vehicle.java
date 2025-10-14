package com.gatherdotech.fleetmanagement.entity;

import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "vehicles",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_vehicles_plate_number", columnNames = "plate_number"),
            @UniqueConstraint(name = "uk_vehicles_vin", columnNames = "vin")
        })
public class Vehicle {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "plate_number", nullable = false, length = 20)
    private String plateNumber;

    @NotBlank
    @Size(min = 17, max = 17)
    @Column(name = "vin", nullable = false, length = 17)
    private String vin; // Vehicle Identification Number

    @NotBlank
    @Size(max = 50)
    @Column(name = "make", nullable = false, length = 50)
    private String make; // e.g., Toyota, Ford

    @NotBlank
    @Size(max = 50)
    @Column(name = "model", nullable = false, length = 50)
    private String model; // e.g., Camry, F-150

    @NotNull
    @Min(1900)
    @Column(name = "\"year\"", nullable = false)
    private Integer year;

    @Size(max = 30)
    @Column(name = "color", length = 30)
    private String color;

    @Min(0)
    @Column(name = "mileage")
    private Integer mileage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    private long version;

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
