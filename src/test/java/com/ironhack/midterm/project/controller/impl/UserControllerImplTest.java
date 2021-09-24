package com.ironhack.midterm.project.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.project.classes.Address;
import com.ironhack.midterm.project.controller.dto.UserDTO;
import com.ironhack.midterm.project.model.users.Admin;
import com.ironhack.midterm.project.model.users.Role;
import com.ironhack.midterm.project.repository.*;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Role roleAdmin;
    private Role roleHolder;
    private Admin admin;

    @BeforeAll
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleHolder = new Role();
        roleHolder.setName("ACCOUNTHOLDER");
        roleRepository.saveAll(List.of(roleAdmin, roleHolder));

        admin = new Admin();
        admin.setName("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole(roleAdmin);
        adminRepository.save(admin);
    }

    @AfterAll
    void tearDown() {
        adminRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        accountHolderRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void store_ValidThirdParty_StatusCreated() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Lucas Sánchez");
        userDTO.setHashedKey("asfd8sjhj8f9999gll");

        String body = objectMapper.writeValueAsString(userDTO);

        MvcResult mvcResult = mockMvc.perform(post("/users").with(httpBasic("admin", "123456"))
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

        MvcResult mvcResult = mockMvc.perform(post("/users").with(httpBasic("admin", "123456"))
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