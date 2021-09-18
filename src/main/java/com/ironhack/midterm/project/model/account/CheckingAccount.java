package com.ironhack.midterm.project.model.account;

import com.ironhack.midterm.project.classes.Money;
import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.users.AccountHolder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class CheckingAccount extends Account {
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "maintenance_fee_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "maintenance_fee_amount"))
    })
    private Money monthlyMaintenanceFee;
    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency")),
        @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount"))
    })
    private Money minimumBalance;
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "penalty_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "penalty_amount"))
    })
    private Money penaltyFee;
    @NotBlank
    private String secretKey;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;


//    public CheckingAccount() { }


    public Money getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
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
