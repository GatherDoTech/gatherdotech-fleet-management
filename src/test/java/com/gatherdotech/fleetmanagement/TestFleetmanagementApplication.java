package com.gatherdotech.fleetmanagement;

import org.springframework.boot.SpringApplication;

public class TestFleetmanagementApplication {

    public static void main(String[] args) {
        SpringApplication.from(FleetmanagementApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
