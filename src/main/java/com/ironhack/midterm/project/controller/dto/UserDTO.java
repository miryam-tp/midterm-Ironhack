package com.ironhack.midterm.project.controller.dto;

import com.ironhack.midterm.project.classes.Address;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public class UserDTO {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Pattern(regexp = "\\d\\d-\\d\\d-\\d\\d\\d\\d", message = "Date format must be DD-MM-YYYY")
    private String dateOfBirth;
    private Address primaryAddress;
    private Address mailingAddress;
    private String hashedKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }
}
