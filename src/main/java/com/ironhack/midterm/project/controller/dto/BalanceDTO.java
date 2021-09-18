package com.ironhack.midterm.project.controller.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BalanceDTO {
    @NotNull
    @Digits(integer = 6, fraction = 2, message = "Wrong balance format")
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
