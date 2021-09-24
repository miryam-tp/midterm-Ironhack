package com.ironhack.midterm.project.model.account;

import com.ironhack.midterm.project.classes.InterestRate;
import com.ironhack.midterm.project.classes.Money;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class CreditCard extends Account {
    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency")),
        @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount"))
    })
    private Money creditLimit;
    @NotNull
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "interest_rate"))
    private InterestRate interestRate;
    @NotNull
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "penalty_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "penalty_amount"))
    })
    private Money penaltyFee;

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
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
}
