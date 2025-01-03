package com.back.portfolio.reopsitories;

import com.back.portfolio.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByNameIgnoreCaseContainingOrSurnameIgnoreCaseContaining(String part, String part1);

    User findByNameIgnoreCaseContainingAndSurnameIgnoreCaseContaining(String name, String surname);
}
