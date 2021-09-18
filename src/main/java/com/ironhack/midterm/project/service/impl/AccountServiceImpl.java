package com.ironhack.midterm.project.service.impl;

import com.ironhack.midterm.project.classes.InterestRate;
import com.ironhack.midterm.project.classes.Money;
import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.account.*;
import com.ironhack.midterm.project.model.users.AccountHolder;
import com.ironhack.midterm.project.repository.*;
import com.ironhack.midterm.project.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    private CheckingAccountRepository checkingAccountRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public Account store(AccountDTO accountDto) {
        if(accountDto.getBalance() == null || accountDto.getPrimaryOwner() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        AccountHolder primaryOwner = accountHolderRepository.findById(accountDto.getPrimaryOwner())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary owner not found"));

        switch (accountDto.getAccountType()) {
            case CREDIT_CARD:
                CreditCard creditCard = new CreditCard();

                //Setting inherited Account properties
                creditCard.setBalance(new Money(accountDto.getBalance()));
                creditCard.setPrimaryOwner(primaryOwner);
                if(accountDto.getSecondaryOwner() != null) {
                    creditCard.setSecondaryOwner(
                            accountHolderRepository.findById(accountDto.getSecondaryOwner())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secondary owner not found"))
                    );
                }
                creditCard.setCreationDate(LocalDate.now());

                //Setting specific CreditCard properties
                if(accountDto.getCreditLimit() == null)
                    creditCard.setCreditLimit(new Money(new BigDecimal("100")));
                else {
                    if(accountDto.getCreditLimit().compareTo(new BigDecimal("100")) < 0 || accountDto.getCreditLimit().compareTo(new BigDecimal("100000")) > 0)
                        creditCard.setCreditLimit(new Money(new BigDecimal("100")));
                    else creditCard.setCreditLimit(new Money(accountDto.getCreditLimit()));
                }

                if(accountDto.getInterestRate() == null)
                    creditCard.setInterestRate(new InterestRate(new BigDecimal("0.2")));
                else {
                    if(accountDto.getInterestRate().compareTo(new BigDecimal("0.1")) < 0)
                        creditCard.setInterestRate(new InterestRate(new BigDecimal("0.2")));
                    else creditCard.setInterestRate(new InterestRate(accountDto.getInterestRate()));
                }

                return creditCardRepository.save(creditCard);

            case CHECKING:
                int ownerAge = LocalDate.now().getYear() - primaryOwner.getDateOfBirth().getYear();
                if(ownerAge < 24) {
                    StudentChecking studentChecking = new StudentChecking();

                    //Setting inherited Account properties
                    studentChecking.setBalance(new Money(accountDto.getBalance()));
                    studentChecking.setPrimaryOwner(primaryOwner);
                    if(accountDto.getSecondaryOwner() != null) {
                        studentChecking.setSecondaryOwner(
                                accountHolderRepository.findById(accountDto.getSecondaryOwner())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secondary owner not found"))
                        );
                    }
                    studentChecking.setCreationDate(LocalDate.now());

                    //Setting specific StudentChecking properties
                    if(accountDto.getSecretKey() == null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Secret key cannot be null");
                    studentChecking.setSecretKey(accountDto.getSecretKey());

                    studentChecking.setStatus(Status.ACTIVE);
                    return studentCheckingRepository.save(studentChecking);
                }
                else {
                    CheckingAccount checkingAccount = new CheckingAccount();

                    //Setting inherited properties
                    checkingAccount.setBalance(new Money(accountDto.getBalance()));
                    checkingAccount.setPrimaryOwner(primaryOwner);
                    if(accountDto.getSecondaryOwner() != null) {
                        checkingAccount.setSecondaryOwner(
                                accountHolderRepository.findById(accountDto.getSecondaryOwner())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secondary owner not found"))
                        );
                    }
                    checkingAccount.setCreationDate(LocalDate.now());

                    //Setting specific CheckingAccount properties
                    if(accountDto.getSecretKey() == null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Secret key cannot be null");

                    checkingAccount.setSecretKey(accountDto.getSecretKey());
                    checkingAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal(12)));
                    checkingAccount.setMinimumBalance(new Money(new BigDecimal(250)));
                    checkingAccount.setPenaltyFee(new Money(new BigDecimal(40)));
                    checkingAccount.setStatus(Status.ACTIVE);

                    return checkingAccountRepository.save(checkingAccount);
                }
            case SAVINGS:
                Savings savings = new Savings();

                //Setting inherited properties
                savings.setBalance(new Money(accountDto.getBalance()));
                savings.setPrimaryOwner(primaryOwner);
                if(accountDto.getSecondaryOwner() != null) {
                    savings.setSecondaryOwner(
                            accountHolderRepository.findById(accountDto.getSecondaryOwner())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secondary owner not found"))
                    );
                }
                savings.setCreationDate(LocalDate.now());

                //Setting specific Savings properties
                if(accountDto.getSecretKey() == null)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Secret key cannot be null");

                if(accountDto.getMinimumBalance() == null)
                    savings.setMinimumBalance(new Money(new BigDecimal("1000")));
                else {
                    if(accountDto.getMinimumBalance().compareTo(new BigDecimal("100")) < 0)
                        savings.setMinimumBalance(new Money(new BigDecimal("1000")));
                    else savings.setMinimumBalance(new Money(accountDto.getMinimumBalance()));
                }

                if(accountDto.getInterestRate() == null)
                    savings.setInterestRate(new InterestRate(new BigDecimal("0.0025")));
                else {
                    if(accountDto.getInterestRate().compareTo(new BigDecimal("0.5")) > 0)
                        savings.setInterestRate(new InterestRate(new BigDecimal("0.0025")));
                    else savings.setInterestRate(new InterestRate(accountDto.getInterestRate()));
                }

                savings.setPenaltyFee(new Money(new BigDecimal(40)));
                savings.setStatus(Status.ACTIVE);
                savings.setSecretKey(accountDto.getSecretKey());

                return savingsRepository.save(savings);
        }
        return null;
    }

    public void updateBalance(Long id, BalanceDTO balanceDto) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        BigDecimal balanceAmount = balanceDto.getAmount();
        if(balanceAmount.compareTo(new BigDecimal(0)) < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New balance cannot be negative");
        account.setBalance(new Money(balanceAmount));
        accountRepository.save(account);
    }


}
