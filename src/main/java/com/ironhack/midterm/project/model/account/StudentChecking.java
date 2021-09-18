package com.ironhack.midterm.project.model.account;

import com.ironhack.midterm.project.classes.Money;
import com.ironhack.midterm.project.enums.Status;
import com.ironhack.midterm.project.model.users.AccountHolder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class StudentChecking extends Account {
    @NotBlank
    private String secretKey;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;

//    public StudentChecking() {
//    }
//
//    public StudentChecking(Long id, Money balance, AccountHolder primaryOwner, String secretKey, Status status) {
//        super(id, balance, primaryOwner);
//        this.secretKey = secretKey;
//        this.status = status;
//    }
//
//    public StudentChecking(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, String secretKey, Status status) {
//        super(balance, primaryOwner, secondaryOwner);
//        this.secretKey = secretKey;
//        this.status = status;
//    }

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
