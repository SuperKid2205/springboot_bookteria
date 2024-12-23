package com.hung.practice.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hung.practice.dto.request.UserCreationRequest;
import com.hung.practice.dto.response.UserResponse;
import com.hung.practice.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;

    @BeforeEach
    void initData() {
        LocalDate dob = LocalDate.of(1991, 5, 22);
        userCreationRequest = UserCreationRequest.builder()
                .username("hung1")
                .password("321")
                .firstName("Hung")
                .lastName("Nguyen")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("95b9f607-9884-4c85-96dd-e5ae8d91f1a6")
                .username("hung1")
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

        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

        // WHEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))

                // THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("95b9f607-9884-4c85-96dd-e5ae8d91f1a6"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("hung1"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.firstName").value("Hung"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.lastName").value("Nguyen"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.dob").value("1991-05-22"));
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {

        userCreationRequest.setUsername("hun");

        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userCreationRequest);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(9003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username must be have least 4 character."));
    }
}
