package com.ironhack.midterm.project.classes;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class InterestRate {
    private BigDecimal amount;

    public InterestRate() {
    }

    public InterestRate(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
