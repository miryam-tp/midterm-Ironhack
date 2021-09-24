package com.ironhack.midterm.project.repository;

import com.ironhack.midterm.project.model.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findDistinctByName(String name);
}
