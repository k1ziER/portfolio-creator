package com.back.portfolio.configurations;

import com.back.portfolio.models.Role;
import com.back.portfolio.models.RoleName;
import com.back.portfolio.reopsitories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (!roleRepository.existsByName(RoleName.ROLE_ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }
        if (!roleRepository.existsByName(RoleName.ROLE_USER)) {
            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            roleRepository.save(userRole);
        }
    }
}

