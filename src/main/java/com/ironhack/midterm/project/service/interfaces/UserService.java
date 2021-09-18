package com.ironhack.midterm.project.service.interfaces;

import com.ironhack.midterm.project.controller.dto.UserDTO;
import com.ironhack.midterm.project.model.users.User;

public interface UserService {
    User store(UserDTO userDto);
}
