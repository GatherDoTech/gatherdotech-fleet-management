package com.gatherdotech.fleetmanagement.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatherdotech.fleetmanagement.enums.DriverStatus;
import com.gatherdotech.fleetmanagement.repository.DriverRepository;
import java.time.LocalDate;
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
class DriverControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired DriverRepository repo;

    private String createBody(
            String firstName, String lastName, String email, String phone, String lic, LocalDate expiry) {
        return """
        {
          "firstName":"%s",
          "lastName":"%s",
          "email":"%s",
          "phone":"%s",
          "licenseNumber":"%s",
          "licenseExpiry":"%s",
          "status":"ACTIVE"
        }
        """
                .formatted(firstName, lastName, email, phone, lic, expiry);
    }

    @BeforeEach
    void clean() {
        repo.deleteAll();
    }

    @Test
    void create_get_update_list_delete_happyPath() throws Exception {
        // CREATE
        String body =
                createBody(
                        "Lindiwe",
                        "Nkosi",
                        "lindiwe.nkosi@example.com",
                        "+27-82-000-0000",
                        "SA-123-456-XYZ",
                        LocalDate.now().plusYears(3));
        String location =
                mockMvc
                        .perform(post("/api/v1/drivers").contentType(MediaType.APPLICATION_JSON).content(body))
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("Location"))
                        .andExpect(jsonPath("$.id", notNullValue()))
                        .andExpect(jsonPath("$.status", is(DriverStatus.ACTIVE.name())))
                        .andReturn()
                        .getResponse()
                        .getHeader("Location");

        String id = location.substring(location.lastIndexOf('/') + 1);

        // GET
        mockMvc
                .perform(get("/api/v1/drivers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("lindiwe.nkosi@example.com")));

        // LIST
        mockMvc
                .perform(get("/api/v1/drivers?status=ACTIVE&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(id)));

        // UPDATE
        String updateBody =
                """
                {
                  "firstName":"Lindiwe",
                  "lastName":"Nkosi",
                  "email":"lnkosi@example.com",
                  "phone":"+27-82-111-2222",
                  "licenseNumber":"SA-123-456-XYZ",
                  "licenseExpiry":"%s",
                  "status":"ACTIVE"
                }
                """
                        .formatted(LocalDate.now().plusYears(4));
        mockMvc
                .perform(
                        put("/api/v1/drivers/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("lnkosi@example.com")));

        // DELETE (deactivate)
        mockMvc.perform(delete("/api/v1/drivers/{id}", id)).andExpect(status().isNoContent());
    }

    @Test
    void create_duplicateEmail_conflict409() throws Exception {
        String body1 =
                createBody(
                        "A",
                        "B",
                        "dup@example.com",
                        "+27-82-1",
                        "LIC-1",
                        LocalDate.now().plusYears(1));
        String body2 =
                createBody(
                        "C",
                        "D",
                        "dup@example.com", // duplicate email
                        "+27-82-2",
                        "LIC-2",
                        LocalDate.now().plusYears(1));

        mockMvc
                .perform(post("/api/v1/drivers").contentType(MediaType.APPLICATION_JSON).content(body1))
                .andExpect(status().isCreated());

        mockMvc
                .perform(post("/api/v1/drivers").contentType(MediaType.APPLICATION_JSON).content(body2))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("CONFLICT")));
    }

    @Test
    void create_invalidExpiry_badRequest400() throws Exception {
        String body =
                createBody(
                        "Bad",
                        "Date",
                        "bad.date@example.com",
                        "+27-82-3",
                        "LIC-3",
                        LocalDate.now().minusDays(1)); // past date

        mockMvc
                .perform(post("/api/v1/drivers").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", anyOf(is("VALIDATION_ERROR"), is("BAD_REQUEST"))));
    }

    @Test
    void get_unknownId_notFound404() throws Exception {
        mockMvc
                .perform(get("/api/v1/drivers/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
