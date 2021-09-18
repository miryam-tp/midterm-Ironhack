package com.ironhack.midterm.project.model.users;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class ThirdParty extends User{
    @NotBlank
    private String hashedKey;

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }
}
