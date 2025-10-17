package com.gatherdotech.fleetmanagement.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatherdotech.fleetmanagement.enums.VehicleStatus;
import com.gatherdotech.fleetmanagement.repository.VehicleRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired VehicleRepository repository;

    private String createBody(
            String plateNumber,
            String vin,
            String make,
            String model,
            Integer year,
            String color,
            Integer mileage) {
        return """
        {
          "plateNumber":"%s",
          "vin":"%s",
          "make":"%s",
          "model":"%s",
          "year":%d,
          "color":"%s",
          "mileage":%d,
          "status":"AVAILABLE"
        }
        """
                .formatted(plateNumber, vin, make, model, year, color, mileage);
    }

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void create_get_update_list_delete_happyPath() throws Exception {
        // CREATE
        String body =
                createBody(
                        "ABC-123-GP",
                        "1HGBH41JXMN109186",
                        "Toyota",
                        "Hilux",
                        2022,
                        "White",
                        50000);

        String location =
                mockMvc
                        .perform(
                                post("/api/v1/vehicles")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(body))
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("Location"))
                        .andExpect(jsonPath("$.id", notNullValue()))
                        .andExpect(jsonPath("$.status", is(VehicleStatus.AVAILABLE.name())))
                        .andExpect(jsonPath("$.plateNumber", is("ABC-123-GP")))
                        .andExpect(jsonPath("$.make", is("Toyota")))
                        .andReturn()
                        .getResponse()
                        .getHeader("Location");

        String id = location.substring(location.lastIndexOf('/') + 1);

        // GET
        mockMvc
                .perform(get("/api/v1/vehicles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber", is("ABC-123-GP")))
                .andExpect(jsonPath("$.vin", is("1HGBH41JXMN109186")))
                .andExpect(jsonPath("$.make", is("Toyota")))
                .andExpect(jsonPath("$.model", is("Hilux")));

        // LIST
        mockMvc
                .perform(get("/api/v1/vehicles?status=AVAILABLE&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(id)));

        // UPDATE
        String updateBody =
                """
                {
                  "plateNumber":"ABC-123-GP",
                  "vin":"1HGBH41JXMN109186",
                  "make":"Toyota",
                  "model":"Hilux",
                  "year":2022,
                  "color":"Silver",
                  "mileage":60000,
                  "status":"IN_USE"
                }
                """;
        mockMvc
                .perform(
                        put("/api/v1/vehicles/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color", is("Silver")))
                .andExpect(jsonPath("$.mileage", is(60000)))
                .andExpect(jsonPath("$.status", is("IN_USE")));

        // DELETE (hard delete)
        mockMvc.perform(delete("/api/v1/vehicles/{id}", id)).andExpect(status().isNoContent());

        // Verify deleted
        mockMvc
                .perform(get("/api/v1/vehicles/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_duplicatePlateNumber_conflict409() throws Exception {
        String body1 =
                createBody(
                        "DUP-999-GP",
                        "1HGBH41JXMN109186",
                        "Ford",
                        "Ranger",
                        2021,
                        "Red",
                        20000);
        String body2 =
                createBody(
                        "DUP-999-GP", // duplicate plate
                        "2HGBH41JXMN109187",
                        "Nissan",
                        "Navara",
                        2020,
                        "Blue",
                        30000);

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body1))
                .andExpect(status().isCreated());

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body2))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("CONFLICT")))
                .andExpect(jsonPath("$.code", is("VEHICLE_PLATE_EXISTS")));
    }

    @Test
    void create_duplicateVin_conflict409() throws Exception {
        String body1 =
                createBody(
                        "XYZ-111-GP",
                        "DUPVIN12345678901",
                        "Honda",
                        "Civic",
                        2019,
                        "Black",
                        40000);
        String body2 =
                createBody(
                        "XYZ-222-GP",
                        "DUPVIN12345678901", // duplicate VIN
                        "Mazda",
                        "CX-5",
                        2020,
                        "Gray",
                        35000);

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body1))
                .andExpect(status().isCreated());

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body2))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("CONFLICT")))
                .andExpect(jsonPath("$.code", is("VEHICLE_VIN_EXISTS")));
    }

    @Test
    void create_invalidYear_badRequest400() throws Exception {
        String body =
                """
                {
                  "plateNumber":"BAD-001-GP",
                  "vin":"1HGBH41JXMN109186",
                  "make":"Toyota",
                  "model":"Corolla",
                  "year":1800,
                  "color":"Red",
                  "mileage":10000,
                  "status":"AVAILABLE"
                }
                """;

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_invalidVinLength_badRequest400() throws Exception {
        String body =
                """
                {
                  "plateNumber":"BAD-002-GP",
                  "vin":"SHORT",
                  "make":"Toyota",
                  "model":"Corolla",
                  "year":2020,
                  "color":"Blue",
                  "mileage":5000,
                  "status":"AVAILABLE"
                }
                """;

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_missingRequiredFields_badRequest400() throws Exception {
        String body =
                """
                {
                  "plateNumber":"",
                  "vin":"",
                  "make":"",
                  "model":""
                }
                """;

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_unknownId_notFound404() throws Exception {
        mockMvc
                .perform(get("/api/v1/vehicles/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_byStatus_filtersCorrectly() throws Exception {
        // Create multiple vehicles with different statuses
        String available1 =
                createBody(
                        "AVL-001-GP",
                        "VIN1234567890AVL1",
                        "Toyota",
                        "Hilux",
                        2022,
                        "White",
                        10000);
        String available2 =
                createBody(
                        "AVL-002-GP",
                        "VIN1234567890AVL2",
                        "Ford",
                        "Ranger",
                        2021,
                        "Blue",
                        20000);

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(available1))
                .andExpect(status().isCreated());

        mockMvc
                .perform(
                        post("/api/v1/vehicles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(available2))
                .andExpect(status().isCreated());

        // List all available vehicles
        mockMvc
                .perform(get("/api/v1/vehicles?status=AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));

        // List all vehicles (no filter)
        mockMvc
                .perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void update_changeStatus_success() throws Exception {
        // Create a vehicle
        String createBody =
                createBody(
                        "UPD-001-GP",
                        "VIN1234567890UPD1",
                        "Toyota",
                        "Corolla",
                        2020,
                        "Silver",
                        30000);

        String location =
                mockMvc
                        .perform(
                                post("/api/v1/vehicles")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createBody))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getHeader("Location");

        String id = location.substring(location.lastIndexOf('/') + 1);

        // Update to IN_MAINTENANCE
        String updateBody =
                """
                {
                  "plateNumber":"UPD-001-GP",
                  "vin":"VIN1234567890UPD1",
                  "make":"Toyota",
                  "model":"Corolla",
                  "year":2020,
                  "color":"Silver",
                  "mileage":35000,
                  "status":"IN_MAINTENANCE"
                }
                """;

        mockMvc
                .perform(
                        put("/api/v1/vehicles/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_MAINTENANCE")))
                .andExpect(jsonPath("$.mileage", is(35000)));
    }
}
