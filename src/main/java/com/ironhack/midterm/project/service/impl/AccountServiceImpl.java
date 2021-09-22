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
import com.ironhack.midterm.project.security.CustomUserDetails;
import com.ironhack.midterm.project.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public BalanceDTO getBalance(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with id " + id + " not found"));

        //Check user authorities
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if(principal instanceof UserDetails) {
            String username = ((CustomUserDetails)principal).getUsername();
            if(auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ACCOUNTHOLDER"))) {
                if(account.getSecondaryOwner() != null) {
                    if(!username.equals(account.getPrimaryOwner().getName()) || !username.equals(account.getSecondaryOwner().getName()))
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                } else {
                    if(!username.equals(account.getPrimaryOwner().getName()))
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                }
            }
        } else {
            String username = principal.toString();
            if(auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ACCOUNTHOLDER"))) {
                if(account.getSecondaryOwner() != null) {
                    if(!username.equals(account.getPrimaryOwner().getName()) || !username.equals(account.getSecondaryOwner().getName()))
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                } else {
                    if(!username.equals(account.getPrimaryOwner().getName()))
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                }
            }
        }

        BalanceDTO balance = new BalanceDTO();
        BigDecimal currentBalance = account.getBalance().getAmount();

        if(account instanceof Savings) {
            Savings savings = savingsRepository.findById(id).get();
            int currentYear = LocalDate.now().getYear();
            int lastInterestYear = savings.getLastAccessed().getYear();
            int yearsPassed = currentYear - lastInterestYear;

            if(yearsPassed > 0) {  //Interest applies annually in Savings accounts
                BigDecimal interest = savings.getInterestRate().getAmount();

                //For each year passed since the last time the account was accessed, we apply the interest rate
                for (int i = 0; i < yearsPassed ; i++) {
                    BigDecimal fees = currentBalance.multiply(interest);  //Calculates fees
                    currentBalance = currentBalance.add(fees);  //Applies fees
                }

                savings.setLastAccessed(LocalDate.now());
                savings.setBalance(new Money(currentBalance));
                savingsRepository.save(savings);
            }
        } else if(account instanceof CreditCard) {
            CreditCard creditCard = creditCardRepository.findById(id).get();

            int currentYear = LocalDate.now().getYear();
            int currentMonth = LocalDate.now().getMonthValue();
            int lastInterestYear = creditCard.getLastAccessed().getYear();
            int lastInterestMonth = creditCard.getLastAccessed().getMonthValue();

            //Interest applies monthly in Credit Card accounts
            if(currentYear - lastInterestYear > 0 || currentMonth - lastInterestMonth > 0) {
                BigDecimal monthlyInterest = creditCard.getInterestRate().getAmount().divide(new BigDecimal("12"));
                int monthsPassed = (currentYear - lastInterestYear) * 12 + currentMonth - lastInterestMonth;

                //For each month passed since the last time the account was accessed, we apply the interest rate
                for(int i = 0; i < monthsPassed; i++) {
                    BigDecimal fees = currentBalance.multiply(monthlyInterest);
                    currentBalance = currentBalance.add(fees);
                }

                creditCard.setLastAccessed(LocalDate.now());
                creditCard.setBalance(new Money(currentBalance));
                creditCardRepository.save(creditCard);
            }
        } else if(account instanceof CheckingAccount) {
            CheckingAccount checkingAccount = checkingAccountRepository.findById(id).get();

            int currentYear = LocalDate.now().getYear();
            int currentMonth = LocalDate.now().getMonthValue();
            int lastInterestYear = checkingAccount.getLastAccessed().getYear();
            int lastInterestMonth = checkingAccount.getLastAccessed().getMonthValue();

            if(currentYear - lastInterestYear > 0 || currentMonth - lastInterestMonth > 0) {
                BigDecimal monthlyFee = checkingAccount.getMonthlyMaintenanceFee().getAmount();
                int monthsPassed = (currentYear - lastInterestYear) * 12 + currentMonth - lastInterestMonth;

                //For each month passed since the last time the account was accessed, we subtract the monthly maintenance fee
                for(int i = 0; i < monthsPassed; i++)
                    currentBalance = currentBalance.subtract(monthlyFee);

                checkingAccount.setLastAccessed(LocalDate.now());
                checkingAccount.setBalance(new Money(currentBalance));
                checkingAccountRepository.save(checkingAccount);
            }
        }

        balance.setAmount(currentBalance.setScale(2, RoundingMode.HALF_EVEN));
        return balance;
    }

    public Account store(AccountDTO accountDto) {
        if(accountDto.getBalance() == null || accountDto.getPrimaryOwner() == null || accountDto.getAccountType() == null)
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
                creditCard.setLastAccessed(LocalDate.now());

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

                creditCard.setPenaltyFee(new Money(new BigDecimal(40)));

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
                    studentChecking.setLastAccessed(LocalDate.now());

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
                    checkingAccount.setLastAccessed(LocalDate.now());

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
                savings.setLastAccessed(LocalDate.now());

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

        //Check if target account exists
        Account account = accountRepository.findById(transferDto.getTargetAccount())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find target account"));

        if(account instanceof CreditCard)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

        if(account instanceof Savings) {
            Savings savings = savingsRepository.findById(account.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            //Check if secret key is valid
            if(!savings.getSecretKey().equals(transferDto.getSecretKey()))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Secret key is not valid");

            //Calculate new balance after transaction
            //If amount is positive, the third party transfers money
            //If amount is negative, the third party receives money
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
            if(!checkingAccount.getSecretKey().equals(transferDto.getSecretKey()))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Secret key is not valid");

            //Calculate new balance after transaction
            //If amount is positive, third party transfers money
            //If amount is negative, third party receives money
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
            if(!studentChecking.getSecretKey().equals(transferDto.getSecretKey()))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Secret key is not valid");

            //Calculate new balance after transaction
            //If amount is negative, third party receives money
            //If amount is positive, third party transfers money
            Money newBalance = new Money(studentChecking.getBalance().getAmount().add(transferDto.getAmount()));

            studentChecking.setBalance(newBalance);
            studentCheckingRepository.save(studentChecking);
        }
    }

    public void transferMoney(TransferDTO transferDto) {
        //Consider transfering money from credit card accounts will increase their balance
        //Should transfering money to a credit card account decrease its balance?
    }
}
