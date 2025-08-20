package com.gatherdotech.fleetmanagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gatherdotech.fleetmanagement.entity.Driver;
import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DriverRepositoryTest {

    @Autowired private DriverRepository repo;

    @Test
    void existsByEmail_and_existsByLicenseNumber() {
        Driver d = new Driver();
        d.setFirstName("Test");
        d.setLastName("User");
        d.setEmail("exists@example.com");
        d.setPhone("+27");
        d.setLicenseNumber("LIC-XYZ");
        d.setLicenseExpiry(LocalDate.now().plusYears(1));
        d.setStatus(DriverStatus.ACTIVE);

        repo.save(d);

        assertThat(repo.existsByEmail("exists@example.com")).isTrue();
        assertThat(repo.existsByEmail("nope@example.com")).isFalse();

        assertThat(repo.existsByLicenseNumber("LIC-XYZ")).isTrue();
        assertThat(repo.existsByLicenseNumber("LIC-ABC")).isFalse();
    }
}
