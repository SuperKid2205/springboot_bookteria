package com.hung.practice.integration.test;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hung.practice.dto.request.UserCreationRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {

    @Container
    static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:latest");

    @Autowired
    private MockMvc mockMvc;

    private UserCreationRequest userCreationRequest;

    @DynamicPropertySource
    static void configDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.driverClassName", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @BeforeEach
    void initData() {
        LocalDate dob = LocalDate.of(1991, 5, 22);
        userCreationRequest = UserCreationRequest.builder()
                .username("hung")
                .password("321")
                .firstName("Hung")
                .lastName("Nguyen")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_requestValid_success() throws Exception {

        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // WHEN
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(content))

                        // THEN
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                //                .andExpect(MockMvcResultMatchers.jsonPath("result.username")
                //                        .value("hung1"))
                //                .andExpect(MockMvcResultMatchers.jsonPath("result.firstName")
                //                        .value("Hung"))
                //                .andExpect(MockMvcResultMatchers.jsonPath("result.lastName")
                //                        .value("Nguyen"))
                //                .andExpect(MockMvcResultMatchers.jsonPath("result.dob")
                //                        .value("1991-05-22"))
                ;
        log.info("Result: " + response.andReturn().getResponse().getContentAsString());
    }
}
