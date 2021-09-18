package com.ironhack.midterm.project.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.project.classes.Address;
import com.ironhack.midterm.project.classes.Money;
import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.enums.AccountType;
import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.account.CheckingAccount;
import com.ironhack.midterm.project.model.users.AccountHolder;
import com.ironhack.midterm.project.model.users.Role;
import com.ironhack.midterm.project.repository.AccountHolderRepository;
import com.ironhack.midterm.project.repository.AccountRepository;
import com.ironhack.midterm.project.repository.CheckingAccountRepository;
import com.ironhack.midterm.project.repository.RoleRepository;
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

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerImplTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

//    private Role role;
    private AccountHolder accountHolder;
    private CheckingAccount checkingAccount;
    private Address address;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

//        role = new Role("Account_Holder");
//        roleRepository.save(role);

        address = new Address("4B", "Sierpes", "Sevilla", "Spain");

        accountHolder = new AccountHolder();
        accountHolder.setName("Lucas Sánchez");
        accountHolder.setDateOfBirth(LocalDate.of(1990, 10, 1));
        accountHolder.setPrimaryAddress(address);
        accountHolder.setMailingAddress(address);
//        accountHolder.setRole(role);
        accountHolderRepository.save(accountHolder);

        checkingAccount = new CheckingAccount();
        checkingAccount.setId(1L);
        checkingAccount.setBalance(new Money(new BigDecimal("340.56")));
        checkingAccount.setPrimaryOwner(accountHolder);
        checkingAccount.setCreationDate(LocalDate.now());
        checkingAccount.setMinimumBalance(new Money(new BigDecimal("250")));
        checkingAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal("12")));
        checkingAccount.setPenaltyFee(new Money(new BigDecimal("40")));
        checkingAccount.setSecretKey("29837");
        checkingAccount.setStatus(Status.ACTIVE);
    }

    @AfterEach
    void tearDown() {

        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
//        roleRepository.deleteAll();
    }

    @Test
    void store_ValidCheckingAccount_Created() throws Exception {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountType.CHECKING);
        accountDTO.setBalance(new BigDecimal("2050.34"));
        accountDTO.setPrimaryOwner(accountHolder.getId());
        accountDTO.setSecretKey("28733");

        String body = objectMapper.writeValueAsString(accountDTO);

        MvcResult mvcResult = mockMvc.perform(post("/accounts")
                            .content(body)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("28733"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Lucas Sánchez"));
    }

    @Test
    void updateBalance_ValidBalance_UpdatedNoContent() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();

        String body = objectMapper.writeValueAsString(balanceDTO);

        mockMvc.perform(put("accounts/1")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    )
                .andExpect(status().isNoContent());
    }

    @Test
    void updateBalance_InvalidBalance_BadRequest() {
    }
}