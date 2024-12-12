package com.sirs.carserver.repository;

import com.sirs.carserver.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    // TODO: Add queries if needed
}
