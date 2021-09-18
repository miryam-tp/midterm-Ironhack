package com.ironhack.midterm.project.service.impl;

import com.ironhack.midterm.project.classes.InterestRate;
import com.ironhack.midterm.project.classes.Money;
import com.ironhack.midterm.project.controller.dto.AccountDTO;
import com.ironhack.midterm.project.controller.dto.BalanceDTO;
import com.ironhack.midterm.project.controller.dto.TransferDTO;
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
import java.util.Optional;

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

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

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

    public void receiveOrTransferMoney(Optional<String> optionalHashedKey, TransferDTO transferDto) {
        //Check if hashed key exists in the request
        String hashedKey = optionalHashedKey
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must provide hashed key in header"));

        //Check if third party user exists
        thirdPartyRepository.findByHashedKey(hashedKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party user with hashed key " + hashedKey + " does not exist"));

        Account account = accountRepository.findById(transferDto.getTargetAccount())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find target account"));

        //TODO: Implement Credit Card handling
//        if(account instanceof CreditCard)
//            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

        if(account instanceof Savings) {
            Savings savings = savingsRepository.findById(account.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            //Check if secret key is valid
            if(savings.getSecretKey() != transferDto.getSecretKey())
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Secret key is not valid");

            //Calculate new balance after transaction
            //If amount is negative, the third party receives money
            //If amount is positive, the third party transfers money
            Money newBalance = new Money(savings.getBalance().getAmount().add(transferDto.getAmount()));

            //Check if balance is lower than minimum balance after transaction
            if(savings.getMinimumBalance().getAmount().compareTo(newBalance.getAmount()) > 0)
                newBalance = new Money(newBalance.getAmount().subtract(savings.getPenaltyFee().getAmount()));  //Apply penalty fee

            //Set balance after transaction
            savings.setBalance(newBalance);
            savingsRepository.save(savings);
        }
        else if(account instanceof CheckingAccount) {
            CheckingAccount checkingAccount = checkingAccountRepository.findById(account.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            //Check if secret key is valid
            if(checkingAccount.getSecretKey() != transferDto.getSecretKey())
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Secret key is not valid");

            //Calculate new balance after transaction
            //If amount is negative, third party receives money
            //If amount is positive, third party transfers money
            Money newBalance = new Money(checkingAccount.getBalance().getAmount().add(transferDto.getAmount()));

            //Check if balance is lower than minimum balance after transaction
            if(checkingAccount.getMinimumBalance().getAmount().compareTo(newBalance.getAmount()) > 0)
                newBalance = new Money(newBalance.getAmount().subtract(checkingAccount.getPenaltyFee().getAmount()));  //Apply penalty fee

            checkingAccount.setBalance(newBalance);
            checkingAccountRepository.save(checkingAccount);
        }
        else if(account instanceof StudentChecking) {
            StudentChecking studentChecking = studentCheckingRepository.findById(account.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            //Check if secret key is valid
            if(studentChecking.getSecretKey() != transferDto.getSecretKey())
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Secret key is not valid");

            //Calculate new balance after transaction
            //If amount is negative, third party receives money
            //If amount is positive, third party transfers money
            Money newBalance = new Money(studentChecking.getBalance().getAmount().add(transferDto.getAmount()));

            studentChecking.setBalance(newBalance);
            studentCheckingRepository.save(studentChecking);
        }
    }


}
