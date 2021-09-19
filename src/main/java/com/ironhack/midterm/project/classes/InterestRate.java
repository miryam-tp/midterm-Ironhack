package com.ironhack.midterm.project.classes;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
public class InterestRate {
    private BigDecimal interest;
    private LocalDate lastInterest;

    public InterestRate() {
    }

    public InterestRate(BigDecimal interest) {
        this.interest = interest;
    }

    public InterestRate(BigDecimal interest, LocalDate lastInterest) {
        this.interest = interest;
        this.lastInterest = lastInterest;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public LocalDate getLastInterest() {
        return lastInterest;
    }

    public void setLastInterest(LocalDate lastInterest) {
        this.lastInterest = lastInterest;
    }
}
