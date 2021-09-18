package com.ironhack.midterm.project.controller.impl;

import com.ironhack.midterm.project.controller.dto.UserDTO;
import com.ironhack.midterm.project.controller.interfaces.UserController;
import com.ironhack.midterm.project.model.users.User;
import com.ironhack.midterm.project.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User store(@RequestBody @Valid UserDTO userDto) {
        return userService.store(userDto);
    }
}
