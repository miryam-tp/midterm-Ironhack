package com.ironhack.midterm.project.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midterm.project.classes.Address;
import com.ironhack.midterm.project.classes.InterestRate;
import com.ironhack.midterm.project.classes.Money;
import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.controller.dto.TransferDTO;
import com.ironhack.midterm.project.enums.AccountType;
import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.account.CheckingAccount;
import com.ironhack.midterm.project.model.account.CreditCard;
import com.ironhack.midterm.project.model.account.Savings;
import com.ironhack.midterm.project.model.account.StudentChecking;
import com.ironhack.midterm.project.model.users.AccountHolder;
import com.ironhack.midterm.project.model.users.Admin;
import com.ironhack.midterm.project.model.users.Role;
import com.ironhack.midterm.project.model.users.ThirdParty;
import com.ironhack.midterm.project.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerImplTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Role role1;
    private Role role2;
    private AccountHolder accountHolder1;
    private AccountHolder accountHolder2;
    private Admin admin;
    private ThirdParty thirdParty;
    private CheckingAccount checkingAccount;
    private StudentChecking studentChecking;
    private CreditCard creditCard;
    private Savings savings;

    private Address address;

    @BeforeAll
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        role1 = new Role("ACCOUNTHOLDER");
        role2 = new Role("ADMIN");
        roleRepository.saveAll(List.of(role1, role2));

        address = new Address("4B", "Sierpes", "Sevilla", "Spain");

        accountHolder1 = new AccountHolder();
        accountHolder1.setName("Lucas Sánchez");
        accountHolder1.setPassword(passwordEncoder.encode("123456"));
        accountHolder1.setRole(role1);
        accountHolder1.setDateOfBirth(LocalDate.of(1990, 10, 1));
        accountHolder1.setPrimaryAddress(address);
        accountHolder1.setMailingAddress(address);
        accountHolderRepository.save(accountHolder1);

        accountHolder2 = new AccountHolder();
        accountHolder2.setName("Laura Reyes");
        accountHolder2.setPassword(passwordEncoder.encode("123456"));
        accountHolder2.setRole(role1);
        accountHolder2.setDateOfBirth(LocalDate.of(LocalDate.now().getYear() - 22, 3, 25));
        accountHolder2.setPrimaryAddress(address);
        accountHolder2.setMailingAddress(address);
        accountHolderRepository.save(accountHolder2);

        admin = new Admin();
        admin.setName("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole(role2);
        adminRepository.save(admin);

        thirdParty = new ThirdParty();
        thirdParty.setName("Third party");
        thirdParty.setHashedKey("138hHLsdF4gpg6777");
        thirdPartyRepository.save(thirdParty);

        checkingAccount = new CheckingAccount();
        checkingAccount.setBalance(new Money(new BigDecimal("340.56")));
        checkingAccount.setPrimaryOwner(accountHolder1);
        checkingAccount.setCreationDate(LocalDate.now());
        checkingAccount.setLastAccessed(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 3, LocalDate.now().getDayOfMonth()));
        checkingAccount.setMinimumBalance(new Money(new BigDecimal("250")));
        checkingAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal("12")));
        checkingAccount.setPenaltyFee(new Money(new BigDecimal("40")));
        checkingAccount.setSecretKey("29837");
        checkingAccount.setStatus(Status.ACTIVE);
        checkingAccountRepository.save(checkingAccount);

        studentChecking = new StudentChecking();
        studentChecking.setBalance(new Money(new BigDecimal("660.7")));
        studentChecking.setPrimaryOwner(accountHolder2);
        studentChecking.setCreationDate(LocalDate.now());
        studentChecking.setLastAccessed(LocalDate.now());
        studentChecking.setSecretKey("AB$334");
        studentChecking.setStatus(Status.ACTIVE);
        studentCheckingRepository.save(studentChecking);

        creditCard = new CreditCard();
        creditCard.setBalance(new Money(new BigDecimal("2000")));
        creditCard.setPrimaryOwner(accountHolder1);
        creditCard.setCreationDate(LocalDate.now());
        creditCard.setLastAccessed(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 3, LocalDate.now().getDayOfMonth()));
        creditCard.setInterestRate(new InterestRate(new BigDecimal("0.12")));
        creditCard.setCreditLimit(new Money(new BigDecimal("500")));
        creditCard.setPenaltyFee(new Money(new BigDecimal("40")));
        creditCardRepository.save(creditCard);

        savings = new Savings();
        savings.setBalance(new Money(new BigDecimal("1500.35")));
        savings.setPrimaryOwner(accountHolder1);
        savings.setCreationDate(LocalDate.now());
        savings.setLastAccessed(LocalDate.of(LocalDate.now().getYear() - 1, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()));
        savings.setSecretKey("123456");
        savings.setStatus(Status.ACTIVE);
        savings.setPenaltyFee(new Money(new BigDecimal("40")));
        savings.setMinimumBalance(new Money(new BigDecimal("1000")));
        savings.setInterestRate(new InterestRate(new BigDecimal("0.2")));
        savingsRepository.save(savings);
    }

    //region getBalance tests
    @Test
    void getBalance_ValidAdminRequestForCheckingAccount_ReturnsBalanceWithFeesApplied() throws Exception {
        //Note that last accessed is set to be three months before the current date
        //Monthly maintenance fee will be applied
        MvcResult mvcResult = mockMvc.perform(get("/accounts/1").with(httpBasic("admin", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("304.56"));
        checkingAccount.setBalance(new Money(new BigDecimal("340.56")));
        checkingAccountRepository.save(checkingAccount);
    }

    @Test
    void getBalance_ValidAccountHolderRequestForCheckingAccount_ReturnsBalance() throws Exception {
        checkingAccount.setLastAccessed(LocalDate.now());
        checkingAccountRepository.save(checkingAccount);

        MvcResult mvcResult = mockMvc.perform(get("/accounts/1").with(httpBasic("Lucas Sánchez", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("340.56"));
    }

    @Test
    void getBalance_ValidRequestForStudentCheckingAccount_ReturnsBalance() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/accounts/2").with(httpBasic("Laura Reyes", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("660.7"));
    }

    @Test
    void getBalance_ValidRequestForCreditCardAccount_ReturnsBalanceWithInterestRateApplied() throws Exception {
        //The credit card's last interest date is set to be three months before the current date
        //Interest rates will be applied for each month
        MvcResult mvcResult = mockMvc.perform(get("/accounts/3").with(httpBasic("Lucas Sánchez", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("2060.60"));
        creditCard.setBalance(new Money(new BigDecimal("2000")));
        creditCardRepository.save(creditCard);
    }

    @Test
    void getBalance_ValidRequestForSavingsAccount_ReturnsBalanceWithInterestRateApplied() throws Exception {
        //The account's last interest date is set to be a year before the current date
        //Interest rates will be applied for each year
        MvcResult mvcResult = mockMvc.perform(get("/accounts/4").with(httpBasic("Lucas Sánchez", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("1800.42"));
        savings.setBalance(new Money(new BigDecimal("1500.35")));
        savingsRepository.save(savings);
    }

    @Test
    void getBalance_InvalidAccountHolderRequestForStudentCheckingAccount_StatusUnauthorized() throws Exception {
        //TODO: Fix authentication check in AccountServiceImpl
        mockMvc.perform(get("/accounts/2").with(httpBasic("Lucas Sánchez", "123456")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBalance_InvalidId_StatusNotFound() throws Exception {
        mockMvc.perform(get("/accounts/0").with(httpBasic("admin", "123456")))
                .andExpect(status().isNotFound());
    }
    //endregion

    //region store tests
    @Test
    void store_ValidCheckingAccount_StatusCreated() throws Exception {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountType.CHECKING);
        accountDTO.setBalance(new BigDecimal("2050.34"));
        accountDTO.setPrimaryOwner(accountHolder1.getId());
        accountDTO.setSecretKey("28733");

        String body = objectMapper.writeValueAsString(accountDTO);

        MvcResult mvcResult = mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
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
    void store_ValidCheckingWithAgeLessThan24_StatusCreated() throws Exception {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountType.CHECKING);
        accountDTO.setBalance(new BigDecimal("200"));
        accountDTO.setPrimaryOwner(accountHolder2.getId());
        accountDTO.setSecretKey("11111");

        String body = objectMapper.writeValueAsString(accountDTO);

        MvcResult mvcResult = mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("11111"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Laura Reyes"));
    }

    @Test
    void store_ValidCreditCardAccount_StatusCreated() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setAccountType(AccountType.CREDIT_CARD);
        accountDto.setBalance(new BigDecimal("0"));
        accountDto.setPrimaryOwner(1L);
        accountDto.setCreditLimit(new BigDecimal("200"));

        String body = objectMapper.writeValueAsString(accountDto);

        MvcResult mvcResult = mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Lucas Sánchez"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("200"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("0.2"));
    }

    @Test
    void store_ValidSavingsAccount_StatusCreated() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setAccountType(AccountType.SAVINGS);
        accountDto.setBalance(new BigDecimal("2100"));
        accountDto.setPrimaryOwner(1L);
        accountDto.setSecretKey("LLLL1");
        accountDto.setMinimumBalance(new BigDecimal("500"));

        String body = objectMapper.writeValueAsString(accountDto);

        MvcResult mvcResult = mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Lucas Sánchez"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("2100"));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("0.0025"));
    }

    @Test
    void store_EmptyBalance_StatusBadRequest() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setAccountType(AccountType.SAVINGS);
        accountDto.setPrimaryOwner(1L);
        accountDto.setSecretKey("LLLL1");
        accountDto.setMinimumBalance(new BigDecimal("500"));

        String body = objectMapper.writeValueAsString(accountDto);

        mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
            .content(body)
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void store_EmptyPrimaryOwner_StatusBadRequest() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setAccountType(AccountType.SAVINGS);
        accountDto.setBalance(new BigDecimal("2100"));
        accountDto.setSecretKey("LLLL1");
        accountDto.setMinimumBalance(new BigDecimal("500"));

        String body = objectMapper.writeValueAsString(accountDto);

        mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void store_EmptyAccountType_StatusBadRequest() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setBalance(new BigDecimal("2100"));
        accountDto.setPrimaryOwner(1L);
        accountDto.setSecretKey("LLLL1");
        accountDto.setMinimumBalance(new BigDecimal("500"));

        String body = objectMapper.writeValueAsString(accountDto);

        mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void store_InvalidPrimaryOwner_StatusNotFound() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setAccountType(AccountType.CHECKING);
        accountDto.setBalance(new BigDecimal("2050.34"));
        accountDto.setPrimaryOwner(999L);
        accountDto.setSecretKey("28733");

        String body = objectMapper.writeValueAsString(accountDto);

        mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void store_InvalidSecondaryOwner_StatusNotFound() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setAccountType(AccountType.CHECKING);
        accountDto.setBalance(new BigDecimal("2050.34"));
        accountDto.setPrimaryOwner(1L);
        accountDto.setSecondaryOwner(999L);
        accountDto.setSecretKey("28733");

        String body = objectMapper.writeValueAsString(accountDto);

        mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void store_InvalidCheckingAccountNoSecretKey_StatusBadRequest() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setAccountType(AccountType.CHECKING);
        accountDto.setBalance(new BigDecimal("2050.34"));
        accountDto.setPrimaryOwner(1L);

        String body = objectMapper.writeValueAsString(accountDto);

        mockMvc.perform(post("/accounts").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void store_InvalidRequest_StatusForbidden() throws Exception {
        AccountDTO accountDto = new AccountDTO();
        accountDto.setAccountType(AccountType.SAVINGS);
        accountDto.setBalance(new BigDecimal("2100"));
        accountDto.setPrimaryOwner(1L);
        accountDto.setSecretKey("LLLL1");
        accountDto.setMinimumBalance(new BigDecimal("500"));

        String body = objectMapper.writeValueAsString(accountDto);

        mockMvc.perform(post("/accounts").with(httpBasic("Laura Reyes", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
                .andExpect(status().isForbidden());
    }

    //endregion

    //region updateBalance tests
    @Test
    void updateBalance_ValidBalance_StatusNoContent() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setAmount(new BigDecimal("600.25"));
        String body = objectMapper.writeValueAsString(balanceDTO);

        mockMvc.perform(put("/accounts/1").with(httpBasic("admin", "123456"))
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)
                    )
                .andExpect(status().isNoContent());

        assertEquals(new BigDecimal("600.25"), accountRepository.findById(1L).get().getBalance().getAmount());
        checkingAccount.setBalance(new Money(new BigDecimal("340.56")));  //Return value to the original balance
    }

    @Test
    void updateBalance_InvalidBalance_StatusBadRequest() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setAmount(new BigDecimal("-100.35"));
        String body = objectMapper.writeValueAsString(balanceDTO);

        mockMvc.perform(put("/accounts/1").with(httpBasic("admin", "123456"))
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

        mockMvc.perform(put("/accounts/200").with(httpBasic("admin", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBalance_InvalidRequest_StatusForbidden() throws Exception {
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setAmount(new BigDecimal("600.25"));
        String body = objectMapper.writeValueAsString(balanceDTO);

        mockMvc.perform(put("/accounts/1").with(httpBasic("Lucas Sánchez", "123456"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isForbidden());
    }

    //endregion

    //region receiveOrTransferMoney tests
    @Test
    void receiveOrTransferMoney_ValidTransferRequest_StatusNoContent() throws Exception {
        checkingAccount.setBalance(new Money(new BigDecimal("340.56")));

        TransferDTO transferDto = new TransferDTO();
        transferDto.setTargetAccount(1L);
        transferDto.setAmount(new BigDecimal("59.44"));
        transferDto.setSecretKey(checkingAccount.getSecretKey());

        String body = objectMapper.writeValueAsString(transferDto);

        mockMvc.perform(put("/accounts/third-party")
                .header("hashedKey", "138hHLsdF4gpg6777")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent());

        assertEquals(new BigDecimal("400.00"), checkingAccountRepository.findById(1L).get().getBalance().getAmount().setScale(2, RoundingMode.HALF_EVEN));
        checkingAccount.setBalance(new Money(new BigDecimal("340.56")));
    }

    @Test
    void receiveOrTransferMoney_ValidReceiveRequest_StatusNoContent() throws Exception {
        savings.setBalance(new Money(new BigDecimal("1500.35")));
        TransferDTO transferDto = new TransferDTO();
        transferDto.setTargetAccount(4L);
        transferDto.setAmount(new BigDecimal("-50.35"));
        transferDto.setSecretKey(savings.getSecretKey());

        String body = objectMapper.writeValueAsString(transferDto);

        mockMvc.perform(put("/accounts/third-party")
                .header("hashedKey", "138hHLsdF4gpg6777")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent());

        assertEquals(new BigDecimal("1450.00"), savingsRepository.findById(4L).get().getBalance().getAmount().setScale(2, RoundingMode.HALF_EVEN));
        savings.setBalance(new Money(new BigDecimal("1500.35")));
    }

    @Test
    void receiveOrTransferMoney_InvalidHashedKey_StatusNotFound() throws Exception {
        TransferDTO transferDto = new TransferDTO();
        transferDto.setTargetAccount(1L);
        transferDto.setAmount(new BigDecimal("60.44"));
        transferDto.setSecretKey("29837");

        String body = objectMapper.writeValueAsString(transferDto);

        mockMvc.perform(put("/accounts/third-party")
                .header("hashedKey", "1212")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void receiveOrTransferMoney_MissingHashedKey_StatusBadRequest() throws Exception {
        TransferDTO transferDto = new TransferDTO();
        transferDto.setTargetAccount(1L);
        transferDto.setAmount(new BigDecimal("60.44"));
        transferDto.setSecretKey("29837");

        String body = objectMapper.writeValueAsString(transferDto);

        mockMvc.perform(put("/accounts/third-party")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void receiveOrTransferMoney_InvalidTargetAccountId_StatusNotFound() throws Exception {
        TransferDTO transferDto = new TransferDTO();
        transferDto.setTargetAccount(666L);
        transferDto.setAmount(new BigDecimal("60.44"));
        transferDto.setSecretKey("29837");

        String body = objectMapper.writeValueAsString(transferDto);

        mockMvc.perform(put("/accounts/third-party")
                .header("hashedKey", "138hHLsdF4gpg6777")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void receiveOrTransferMoney_InvalidSecretKey_StatusUnprocessableEntity() throws Exception {
        TransferDTO transferDto = new TransferDTO();
        transferDto.setTargetAccount(1L);
        transferDto.setAmount(new BigDecimal("60.44"));
        transferDto.setSecretKey("111");

        String body = objectMapper.writeValueAsString(transferDto);

        mockMvc.perform(put("/accounts/third-party")
                .header("hashedKey", "138hHLsdF4gpg6777")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void receiveOrTransferMoney_TargetAccountIsCreditCard_StatusUnprocessableEntity() throws Exception {
        TransferDTO transferDto = new TransferDTO();
        transferDto.setTargetAccount(3L);
        transferDto.setAmount(new BigDecimal("60.44"));
        transferDto.setSecretKey("29837");

        String body = objectMapper.writeValueAsString(transferDto);

        mockMvc.perform(put("/accounts/third-party")
                .header("hashedKey", "138hHLsdF4gpg6777")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isUnprocessableEntity());
    }
    //endregion
}