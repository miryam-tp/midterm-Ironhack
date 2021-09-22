package com.ironhack.midterm.project.controller.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferDTO {
    //Both
    @NotNull
    private Long targetAccount;
    @NotNull
    private BigDecimal amount;

    //Only thirdparty request
    private String secretKey;

    //Only accountholder request
    private Long originAccount;
    private String accountOwner;

    public Long getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(Long targetAccount) {
        this.targetAccount = targetAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Long getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(Long originAccount) {
        this.originAccount = originAccount;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }
}
