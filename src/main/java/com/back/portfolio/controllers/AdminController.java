package com.back.portfolio.controllers;

import com.back.portfolio.dao.Peoples;
import com.back.portfolio.models.Role;
import com.back.portfolio.models.RoleName;
import com.back.portfolio.models.User;
import com.back.portfolio.reopsitories.RoleRepository;
import com.back.portfolio.reopsitories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
public class AdminController {
    private final Peoples user;
    private final UserRepository usersRepositories;
    private final RoleRepository rolesRepositories;

    @GetMapping("/admin")
    public String admin(Model model) {
       model.addAttribute("user", user.getAll());
       return "admin";
    }
    @PostMapping("/admin/user/ban/{id}")
    public String userBan(@PathVariable("id") Long id) {
        user.banUser(id);
        return "redirect:/admin";
    }
    @GetMapping("/admin/user/edit/{user}")
    public String userEdit(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", RoleName.values());
        return "userEdit";
    }
    @PostMapping("/admin/user/edit")
    public String updateUser(@RequestParam("userId") Long userId,
                             @RequestParam(value = "roles", required = false) RoleName[] roles) {
        User us = user.getIdUser(userId);

        us.getRoles().clear();

        if (roles != null) {
            for (RoleName roleName : roles) {
                Optional<Role> role = rolesRepositories.findByName(roleName);
                Role rl = role.get();
                us.getRoles().add(rl);
            }
        }

        usersRepositories.save(us);
        return "redirect:/admin";
    }
}
