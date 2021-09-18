package com.ironhack.midterm.project.model.account;

import com.ironhack.midterm.project.classes.InterestRate;
import com.ironhack.midterm.project.classes.Money;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    private InterestRate interestRate;

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
}
