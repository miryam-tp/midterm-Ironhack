package com.ironhack.midterm.project.classes;

import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.account.CheckingAccount;
import com.ironhack.midterm.project.model.users.AccountHolder;
import com.ironhack.midterm.project.repository.AccountHolderRepository;
import com.ironhack.midterm.project.repository.CheckingAccountRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FraudDetectorTest {

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    private FraudDetector fraudDetector;
    private AccountHolder accountHolder;
    private CheckingAccount checkingAccount;

    private LocalDate currentDate = LocalDate.now();
    private LocalTime twoSecondsAgoTime = LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond() - 2);

    @BeforeAll
    void setUp() {
        fraudDetector = new FraudDetector(LocalDateTime.now(), new Money(new BigDecimal("50")), new BigDecimal("500.50"), new BigDecimal("200"));

        accountHolder = new AccountHolder();
        accountHolder.setName("Lucas Sánchez");
        accountHolder.setDateOfBirth(LocalDate.of(1980, 8, 23));
        accountHolder.setPrimaryAddress(new Address("4", "Sierpes", "Sevilla", "Spain"));
        accountHolder.setMailingAddress(new Address("4", "Sierpes", "Sevilla", "Spain"));
        accountHolderRepository.save(accountHolder);

        checkingAccount = new CheckingAccount();
        checkingAccount.setBalance(new Money(new BigDecimal("500.80")));
        checkingAccount.setPrimaryOwner(accountHolder);
        checkingAccount.setSecretKey("1234");
        checkingAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal("12")));
        checkingAccount.setMinimumBalance(new Money(new BigDecimal("250")));
        checkingAccount.setStatus(Status.ACTIVE);
        checkingAccount.setPenaltyFee(new Money(new BigDecimal("40")));
        checkingAccount.setCreationDate(LocalDate.now());
        checkingAccount.setLastAccessed(LocalDate.now());
        checkingAccount.setFraudDetector(fraudDetector);
        checkingAccountRepository.save(checkingAccount);
    }

    @Test
    void checkThirdPartyTransaction() {
    }

    @Test
    void checkAccountHolderTransaction() {
    }

    @Test
    void lastTransactionLessThanOneSecondAgo_TwoSecondsAgo_ReturnFalse() {
        FraudDetector fraudDetector = checkingAccount.getFraudDetector();
        fraudDetector.setLastTransactionTime(LocalDateTime.of(currentDate, twoSecondsAgoTime));
        checkingAccountRepository.save(checkingAccount);
        assertFalse(fraudDetector.isLastTransactionLessThanOneSecondAgo());
    }

    @Test
    void lastTransactionLessThanOneSecondAgo_LessThanOneSecondAgo_ReturnTrue() {
        FraudDetector fraudDetector = checkingAccount.getFraudDetector();
        fraudDetector.setLastTransactionTime(LocalDateTime.now());
        checkingAccountRepository.save(checkingAccount);
        assertTrue(fraudDetector.isLastTransactionLessThanOneSecondAgo());
    }
}