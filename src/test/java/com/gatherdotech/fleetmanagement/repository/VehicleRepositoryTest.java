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
        Vehicle v = new Vehicle();
        v.setPlateNumber("TEST-123");
        v.setVin("1HGBH41JXMN109186");
        v.setMake("Toyota");
        v.setModel("Corolla");
        v.setYear(2020);
        v.setStatus(VehicleStatus.AVAILABLE);

        entityManager.persistAndFlush(v);

        assertThat(repository.existsByPlateNumber("TEST-123")).isTrue();
        assertThat(repository.existsByPlateNumber("NOPE-999")).isFalse();

        assertThat(repository.existsByVin("1HGBH41JXMN109186")).isTrue();
        assertThat(repository.existsByVin("1HGBH41JXMN000000")).isFalse();
    }
}
