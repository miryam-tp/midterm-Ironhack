package com.ironhack.midterm.project.controller.dto;

import com.ironhack.midterm.project.enums.AccountType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AccountDTO {
    @NotNull
    private AccountType accountType;
    @NotNull
    @DecimalMin("0")
    @Digits(integer = 6, fraction = 2, message = "Wrong balance format")
    private BigDecimal balance;
    @NotNull
    @DecimalMin("1")
    private Long primaryOwner;
    @DecimalMin("1")
    private Long secondaryOwner;

    //Status will be set ACTIVE
    //PenaltyFee is 40 for all accounts
    //MonthlyMaintenanceFee is 12 for all Checking Accounts
    private String secretKey;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private BigDecimal creditLimit;  //Only for CreditCard Accounts

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(Long primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public Long getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(Long secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
}
