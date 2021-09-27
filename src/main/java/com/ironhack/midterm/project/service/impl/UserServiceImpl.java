package com.ironhack.midterm.project.service.impl;

import com.ironhack.midterm.project.controller.dto.UserDTO;
import com.ironhack.midterm.project.model.users.AccountHolder;
import com.ironhack.midterm.project.model.users.ThirdParty;
import com.ironhack.midterm.project.model.users.User;
import com.ironhack.midterm.project.repository.AccountHolderRepository;
import com.ironhack.midterm.project.repository.RoleRepository;
import com.ironhack.midterm.project.repository.ThirdPartyRepository;
import com.ironhack.midterm.project.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public User store(UserDTO userDto) {
        if(userDto.getHashedKey() != null) {
            ThirdParty thirdParty = new ThirdParty();
            thirdParty.setHashedKey(userDto.getHashedKey());

            thirdParty.setName(userDto.getName());
            return thirdPartyRepository.save(thirdParty);
        }
        else {
            AccountHolder accountHolder = new AccountHolder();
            accountHolder.setName(userDto.getName());

            if(userDto.getPassword() != null) accountHolder.setPassword(passwordEncoder.encode(userDto.getPassword()));
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty");

            if(userDto.getDateOfBirth() != null){
                String[] dateFromString = userDto.getDateOfBirth().split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(dateFromString[2]), Integer.parseInt(dateFromString[1]), Integer.parseInt(dateFromString[0]));
                accountHolder.setDateOfBirth(date);
            }
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date of birth cannot be empty");

            if(userDto.getPrimaryAddress() != null) accountHolder.setPrimaryAddress(userDto.getPrimaryAddress());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Primary address cannot be empty");

            if(userDto.getMailingAddress() != null) accountHolder.setMailingAddress(userDto.getMailingAddress());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mailing address cannot be empty");

            accountHolder.setRole(roleRepository.findDistinctByName("ACCOUNTHOLDER").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")));

            return accountHolderRepository.save(accountHolder);
        }
    }
}
