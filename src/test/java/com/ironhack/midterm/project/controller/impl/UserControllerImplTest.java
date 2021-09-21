package com.ironhack.midterm.project.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.project.classes.Address;
import com.ironhack.midterm.project.controller.dto.UserDTO;
import com.ironhack.midterm.project.repository.AccountHolderRepository;
import com.ironhack.midterm.project.repository.ThirdPartyRepository;
import com.ironhack.midterm.project.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
    }

    //TODO: Add basic auth to test

    @Test
    void store_ValidThirdParty_StatusCreated() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Lucas Sánchez");
        userDTO.setHashedKey("asfd8sjhj8f9999gll");

        String body = objectMapper.writeValueAsString(userDTO);

        MvcResult mvcResult = mockMvc.perform(post("/users")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Lucas Sánchez"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("asfd8sjhj8f9999gll"));
    }

    @Test
    void store_ValidAccountHolder_StatusCreated() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Laura Martínez");
        userDTO.setDateOfBirth("05-07-1980");
        userDTO.setPrimaryAddress(new Address("4B", "Sierpes", "Sevilla", "Spain"));
        userDTO.setMailingAddress(new Address("4B", "Sierpes", "Sevilla", "Spain"));

        String body = objectMapper.writeValueAsString(userDTO);

        MvcResult mvcResult = mockMvc.perform(post("/users")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Laura Martínez"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("1980"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Sevilla"));
    }
}