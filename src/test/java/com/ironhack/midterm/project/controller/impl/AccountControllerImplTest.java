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
import org.junit.jupiter.api.AfterAll;
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
        checkingAccount.setBalance(new Money(new BigDecimal("340.56")));
        checkingAccount.setPrimaryOwner(accountHolder);
        checkingAccount.setCreationDate(LocalDate.now());
        checkingAccount.setMinimumBalance(new Money(new BigDecimal("250")));
        checkingAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal("12")));
        checkingAccount.setPenaltyFee(new Money(new BigDecimal("40")));
        checkingAccount.setSecretKey("29837");
        checkingAccount.setStatus(Status.ACTIVE);
        checkingAccountRepository.save(checkingAccount);
    }

    @AfterEach
    void tearDown() {
        checkingAccountRepository.deleteAll();
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
//        roleRepository.deleteAll();
    }

    //TODO: getBalance tests

    @Test
    void store_ValidCheckingAccount_StatusCreated() throws Exception {
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

    //TODO: Test de store con CreditCard

    //TODO: Test de store con Savings

    //TODO: Test de store con Checking con usuario menor de 24 años

    @Test
    void updateBalance_ValidBalance_StatusNoContent() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setAmount(new BigDecimal("600.25"));
        String body = objectMapper.writeValueAsString(balanceDTO);

        mockMvc.perform(put("/accounts/1")
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    )
                .andExpect(status().isNoContent());

        assertEquals(new BigDecimal("600.25"), accountRepository.findById(1L).get().getBalance().getAmount());
    }

    @Test
    void updateBalance_InvalidBalance_StatusBadRequest() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setAmount(new BigDecimal("-100.35"));  //Al poner validación en el DTO a lo mejor esto da error ahora, cuidado
        String body = objectMapper.writeValueAsString(balanceDTO);

        mockMvc.perform(put("/accounts/2")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBalance_InvalidId_StatusNotFound() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setAmount(new BigDecimal("1000"));
        String body = objectMapper.writeValueAsString(balanceDTO);

        mockMvc.perform(put("/accounts/200")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    //TODO: receiveOrTransferMoney tests
    @Test
    void receiveOrTransferMoney_ValidRequest_StatusNoContent() {

    }
}