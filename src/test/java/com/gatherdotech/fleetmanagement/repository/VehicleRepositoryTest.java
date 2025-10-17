package com.gatherdotech.fleetmanagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gatherdotech.fleetmanagement.entity.Vehicle;
import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private VehicleRepository repository;

    @Test
    void existsByPlateNumber_and_existsByVin() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber("TEST-123");
        vehicle.setVin("1HGBH41JXMN109186");
        vehicle.setMake("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setYear(2020);
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        entityManager.persistAndFlush(vehicle);

        assertThat(repository.existsByPlateNumber("TEST-123")).isTrue();
        assertThat(repository.existsByPlateNumber("NOPE-999")).isFalse();

        assertThat(repository.existsByVin("1HGBH41JXMN109186")).isTrue();
        assertThat(repository.existsByVin("1HGBH41JXMN000000")).isFalse();
    }
}
