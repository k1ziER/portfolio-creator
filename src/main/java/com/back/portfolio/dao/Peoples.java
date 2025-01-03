package com.back.portfolio.dao;

import com.back.portfolio.models.*;
import com.back.portfolio.reopsitories.FileRepository;
import com.back.portfolio.reopsitories.RoleRepository;
import com.back.portfolio.reopsitories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class Peoples {

    private final UserRepository usersRepositories;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;


    public boolean createPeoples( User user) {
        if (usersRepositories.findByEmail(user.getEmail()) != null) {
            return false;
        }
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Роль ROLE_ADMIN не найдена"));
        user.getRoles().add(adminRole);
        usersRepositories.save(user);
        return true;
    }

    public List<User> getAll() {
        return usersRepositories.findAll();
    }

    public User getIdUser(Long id) {
        return usersRepositories.findById(id).orElse(null);

    }

    public User findByEmail(String user) {
        User us = usersRepositories.findByEmail(user);
        return us;
    }

    public void banUser(Long id){
        User user = usersRepositories.findById(id).orElse(null);
        if(user != null){
            if(user.isActive()){
                user.setActive(false);
                log.info("Banned user " + user.getEmail());
            } else {
                user.setActive(true);
                log.info("Unbanned user " + user.getEmail());
            }
            usersRepositories.save(user);
        }
    }

    public void configureFiles(MultipartFile multipartFile, File file) throws IOException {
        file.setFileName(multipartFile.getOriginalFilename());
        file.setBytes(multipartFile.getBytes());
        file.setSize(multipartFile.getSize());
        file.setContentType(multipartFile.getContentType());
    }

    public void configureImages(MultipartFile multipartFile, Image image) throws IOException {
        image.setOriginalFileName(multipartFile.getOriginalFilename());
        image.setBytes(multipartFile.getBytes());
        image.setSize(multipartFile.getSize());
        image.setContentType(multipartFile.getContentType());
    }

    public void autoLogin(String username, String password, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);


            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
        } catch (Exception e) {
            System.out.println("Ошибка аутентификации: " + e.getMessage());
        }
    }

    public void update(User existingUser, User updatedUser) {
        existingUser.setDescription(updatedUser.getDescription());
        existingUser.setName(updatedUser.getName());
        existingUser.setSurname(updatedUser.getSurname());
        existingUser.setPhone(updatedUser.getPhone());
        usersRepositories.save(existingUser);
    }

    public void deleteUser(User user) {
        usersRepositories.delete(user);
    }


    public User searchByNameOrSurname(String part) {
        return usersRepositories.findByNameIgnoreCaseContainingOrSurnameIgnoreCaseContaining(part, part);
    }

    public User searchByNameAndSurname(String name, String surname) {
        return usersRepositories.findByNameIgnoreCaseContainingAndSurnameIgnoreCaseContaining(name, surname);
    }
}
