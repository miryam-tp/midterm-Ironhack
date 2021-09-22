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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private RoleRepository roleRepository;

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

            if(userDto.getDateOfBirth() != null){
                String[] dateFromString = userDto.getDateOfBirth().split("-");
                LocalDate date = LocalDate.of(Integer.valueOf(dateFromString[2]), Integer.valueOf(dateFromString[1]), Integer.valueOf(dateFromString[0]));
                accountHolder.setDateOfBirth(date);
            }
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            if(userDto.getPrimaryAddress() != null) accountHolder.setPrimaryAddress(userDto.getPrimaryAddress());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            if(userDto.getMailingAddress() != null) accountHolder.setMailingAddress(userDto.getMailingAddress());
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            accountHolder.setRole(roleRepository.findById(1L).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")));

            return accountHolderRepository.save(accountHolder);
        }
    }
}
