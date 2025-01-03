package com.back.portfolio.reopsitories;

import com.back.portfolio.models.Role;
import com.back.portfolio.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);

    boolean existsByName(RoleName roleUser);
}
