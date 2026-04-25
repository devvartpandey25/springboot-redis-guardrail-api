package com.grid07.assignment.repository;

import com.grid07.assignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// repository for User entity
public interface UserRepository extends JpaRepository<User, Long> {

}

