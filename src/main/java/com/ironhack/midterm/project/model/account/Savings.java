package com.ironhack.midterm.project.model.account;

import com.ironhack.midterm.project.classes.InterestRate;
import com.ironhack.midterm.project.classes.Money;
import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.users.AccountHolder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Savings extends Account {
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount"))
    })
    private Money minimumBalance;
    @NotNull
    @Embedded
    private InterestRate interestRate;
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "penalty_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "penalty_amount"))
    })
    private Money penaltyFee = new Money(new BigDecimal(40), Money.EUR);
    @NotBlank
    private String secretKey;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;

    //region Constructors
//    public Savings() {
//    }
//
//    public Savings(Long id, Money balance, AccountHolder primaryOwner, String secretKey, Status status) {
//        super(id, balance, primaryOwner);
//        this.minimumBalance = new Money(new BigDecimal(1000), Money.EUR);
//        this.interestRate = new InterestRate(new BigDecimal(0.0025));
//        this.secretKey = secretKey;
//        this.status = status;
//    }
//
//    public Savings(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, String secretKey, Status status) {
//        super(balance, primaryOwner, secondaryOwner);
//        this.minimumBalance = new Money(new BigDecimal(1000), Money.EUR);
//        this.interestRate = new InterestRate(new BigDecimal(0.0025));
//        this.secretKey = secretKey;
//        this.status = status;
//    }
//
//    public Savings(Long id, Money balance, AccountHolder primaryOwner, InterestRate interestRate, String secretKey, Status status) {
//        super(id, balance, primaryOwner);
//        this.minimumBalance = new Money(new BigDecimal(1000), Money.EUR);
//        if(interestRate.getInterest().compareTo(new BigDecimal(0)) < 0 || interestRate.getInterest().compareTo(new BigDecimal(0.5)) > 0)
//            this.interestRate = new InterestRate(new BigDecimal(0.0025));
//        else this.interestRate = interestRate;
//        this.secretKey = secretKey;
//        this.status = status;
//    }
//
//    public Savings(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, InterestRate interestRate, String secretKey, Status status) {
//        super(balance, primaryOwner, secondaryOwner);
//        this.minimumBalance = new Money(new BigDecimal(1000), Money.EUR);
//        if(interestRate.getInterest().compareTo(new BigDecimal(0)) < 0 || interestRate.getInterest().compareTo(new BigDecimal(0.5)) > 0)
//            this.interestRate = new InterestRate(new BigDecimal(0.0025));
//        else this.interestRate = interestRate;
//        this.secretKey = secretKey;
//        this.status = status;
//    }
//
//    public Savings(Long id, Money balance, AccountHolder primaryOwner, Money minimumBalance, String secretKey, Status status) {
//        super(id, balance, primaryOwner);
//        if(minimumBalance.getAmount().compareTo(new BigDecimal(100)) < 0 || minimumBalance.getAmount().compareTo(new BigDecimal(1000)) > 0)
//            this.minimumBalance = new Money(new BigDecimal(1000), Money.EUR);
//        else this.minimumBalance = minimumBalance;
//        this.interestRate = new InterestRate(new BigDecimal(0.0025));
//        this.secretKey = secretKey;
//        this.status = status;
//    }
//
//    public Savings(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, Money minimumBalance, String secretKey, Status status) {
//        super(balance, primaryOwner, secondaryOwner);
//        if(minimumBalance.getAmount().compareTo(new BigDecimal(100)) < 0 || minimumBalance.getAmount().compareTo(new BigDecimal(1000)) > 0)
//            this.minimumBalance = new Money(new BigDecimal(1000), Money.EUR);
//        else this.minimumBalance = minimumBalance;
//        this.interestRate = new InterestRate(new BigDecimal(0.0025));
//        this.secretKey = secretKey;
//        this.status = status;
//    }
//
//    public Savings(Long id, Money balance, AccountHolder primaryOwner, Money minimumBalance, InterestRate interestRate, String secretKey, Status status) {
//        super(id, balance, primaryOwner);
//        if(minimumBalance.getAmount().compareTo(new BigDecimal(100)) < 0 || minimumBalance.getAmount().compareTo(new BigDecimal(1000)) > 0)
//            this.minimumBalance = new Money(new BigDecimal(1000), Money.EUR);
//        else this.minimumBalance = minimumBalance;
//        if(interestRate.getInterest().compareTo(new BigDecimal(0)) < 0 || interestRate.getInterest().compareTo(new BigDecimal(0.5)) > 0)
//            this.interestRate = new InterestRate(new BigDecimal(0.0025));
//        else this.interestRate = interestRate;
//        this.secretKey = secretKey;
//        this.status = status;
//    }
//
//    public Savings(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, Money minimumBalance, InterestRate interestRate, String secretKey, Status status) {
//        super(balance, primaryOwner, secondaryOwner);
//        if(minimumBalance.getAmount().compareTo(new BigDecimal(100)) < 0 || minimumBalance.getAmount().compareTo(new BigDecimal(1000)) > 0)
//            this.minimumBalance = new Money(new BigDecimal(1000), Money.EUR);
//        else this.minimumBalance = minimumBalance;
//        if(interestRate.getInterest().compareTo(new BigDecimal(0)) < 0 || interestRate.getInterest().compareTo(new BigDecimal(0.5)) > 0)
//            this.interestRate = new InterestRate(new BigDecimal(0.0025));
//        else this.interestRate = interestRate;
//        this.secretKey = secretKey;
//        this.status = status;
//    }
    //endregion

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public InterestRate getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(InterestRate interestRate) {
        this.interestRate = interestRate;
    }

    public Money getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(Money penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
