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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    private LocalTime twoSecondsAgoTime = LocalTime.now().minusSeconds(2);

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
    void checkTransaction_NotFraudulent_AccountStaysActive() {
        checkingAccount.setStatus(Status.ACTIVE);
        checkingAccountRepository.save(checkingAccount);
        FraudDetector.checkTransaction(checkingAccount, new BigDecimal("20.99"));
        assertEquals(Status.ACTIVE, checkingAccount.getStatus());
    }

    @Test
    void checkTransaction_FraudulentAmountTooBig_StatusLockedAndAccountGetsFrozen() {
        checkingAccount.setStatus(Status.ACTIVE);
        checkingAccount.setFraudDetector(new FraudDetector(LocalDateTime.now().minusHours(2), new Money(new BigDecimal("20")), new BigDecimal("20"), new BigDecimal("10")));
        checkingAccountRepository.save(checkingAccount);

        assertThrows(ResponseStatusException.class, () -> FraudDetector.checkTransaction(checkingAccount, new BigDecimal("89.99")));
        assertEquals(Status.FROZEN, checkingAccount.getStatus());
    }

    @Test
    void checkTransaction_FraudulentTooManyRequests_AccountGetsFrozen() {
        checkingAccount.setStatus(Status.ACTIVE);
        checkingAccount.setFraudDetector(new FraudDetector(LocalDateTime.now(), new Money(new BigDecimal("20")), new BigDecimal("800"), new BigDecimal("250")));
        checkingAccountRepository.save(checkingAccount);

        assertThrows(ResponseStatusException.class, () -> FraudDetector.checkTransaction(checkingAccount, new BigDecimal("100")));
        assertEquals(Status.FROZEN, checkingAccount.getStatus());
    }

    @Test
    void isMoreThanHighestDailyTransactions_AmountSmaller_ReturnFalse() {
        checkingAccount.setFraudDetector(new FraudDetector(
                LocalDateTime.of(LocalDate.now(), twoSecondsAgoTime), new Money(new BigDecimal("50")), new BigDecimal("200"), new BigDecimal("100")
                ));
        checkingAccountRepository.save(checkingAccount);
        FraudDetector fraudDetector = checkingAccount.getFraudDetector();
        assertFalse(fraudDetector.isMoreThanHighestDailyTransactions(new BigDecimal(100)));
    }

    @Test
    void isMoreThanHighestDailyTransactions_NoMaxDailyTransactionsSet_ReturnFalse() {
        checkingAccount.setFraudDetector(new FraudDetector(new BigDecimal("0"), new BigDecimal("0")));
        checkingAccountRepository.save(checkingAccount);
        FraudDetector fraudDetector = checkingAccount.getFraudDetector();
        assertFalse(fraudDetector.isMoreThanHighestDailyTransactions(new BigDecimal("200")));
    }

    @Test
    void isMoreThanHighestDailyTransactions_AmountBiggerAndNoTransactionsTheSameDay_ReturnFalse() {
        checkingAccount.setFraudDetector(new FraudDetector(
                LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.now()), new Money(new BigDecimal("50")), new BigDecimal("200"), new BigDecimal("100")
        ));
        checkingAccountRepository.save(checkingAccount);
        FraudDetector fraudDetector = checkingAccount.getFraudDetector();
        assertFalse(fraudDetector.isMoreThanHighestDailyTransactions(new BigDecimal(400)));
    }

    @Test
    void isMoreThanHighestDailyTransactions_AmountBigger_ReturnTrue() {
        checkingAccount.setFraudDetector(new FraudDetector(
                LocalDateTime.now().minusHours(2), new Money(new BigDecimal("50")), new BigDecimal("200"), new BigDecimal("100")
        ));
        checkingAccountRepository.save(checkingAccount);
        FraudDetector fraudDetector = checkingAccount.getFraudDetector();
        assertTrue(fraudDetector.isMoreThanHighestDailyTransactions(new BigDecimal(400)));
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