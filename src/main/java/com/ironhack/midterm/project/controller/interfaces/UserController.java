package com.ironhack.midterm.project.controller.interfaces;

import com.ironhack.midterm.project.controller.dto.UserDTO;
import com.ironhack.midterm.project.model.users.User;

public interface UserController {
    User store(UserDTO userDto);
}
