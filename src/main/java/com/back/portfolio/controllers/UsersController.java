package com.back.portfolio.controllers;

import com.back.portfolio.dao.Peoples;
import com.back.portfolio.models.File;
import com.back.portfolio.models.Image;
import com.back.portfolio.models.User;
import com.back.portfolio.reopsitories.UserRepository;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UsersController {
    private final Peoples service;

    @GetMapping("/login")
    public String login (Model model) {
        model.addAttribute("enter", new User());
        return "login";
    }
    @PostMapping("/login")
    public String login (@ModelAttribute("enter") User usr){
        return "profile" + usr.getId() ;
    }
    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("people", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String register(@Valid @ModelAttribute("people") User user, BindingResult bindingResult, Model model, @RequestParam("multipartFiles") MultipartFile[] multipartFiles, HttpServletRequest request) throws IOException {

        MultipartFile multipartFile = user.getImageFile();
        if (multipartFile != null && !multipartFile.isEmpty()) {
            try {
                if (!multipartFile.getContentType().startsWith("image/")) {
                    bindingResult.rejectValue("imageFile", "error.people", "Неверный тип файла");
                    return "registration";
                }
                long MAX_SIZE = 10 * 1960 * 1960; // 5MB
                if (multipartFile.getSize() > MAX_SIZE) {
                    bindingResult.rejectValue("imageFile", "error.people", "Файл слишком большой");
                    return "registration";
                }
                Image image = new Image();
                service.configureImages(multipartFile, image);

                user.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
                bindingResult.rejectValue("imageFile", "error.people", "Ошибка при загрузке файла");
                return "registration";
            }
        }
        List<File> files = new ArrayList<>();
        if(multipartFiles != null && multipartFiles.length > 0) {
            for(MultipartFile multipartFile1 : multipartFiles) {
                try {
                    if (!multipartFile1.getContentType().startsWith("application/") && !multipartFile1.getContentType().startsWith("text/")) {
                        bindingResult.rejectValue("file", "error.people", "Неверный тип файла");
                        return "registration";
                    }
                    File file = new File();
                    service.configureFiles(multipartFile1, file);
                    files.add(file);
                }catch (IOException e) {
                    e.printStackTrace();
                    bindingResult.rejectValue("file", "error.people", "Ошибка при загрузке файла");
                    return "registration";
                }
            }
        }
        user.setFiles(files);

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        String rawPassword = user.getPassword();
        if(!service.createPeoples(user)) {
            model.addAttribute("errorMessage", "Пользователь с таким email уже существует");
            return "registration";
        }
        service.autoLogin(user.getEmail(), rawPassword, request);
        return "redirect:/profile/" + user.getId();
    }

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable(value = "id") Long id, Model model) {

        User user = service.getIdUser(id);
        if (user != null) {
            model.addAttribute("people", user);
            return "profile";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/profile/{id}/edit")
    public String editProfile(@PathVariable(value = "id") Long id, Model model) {
        User user = service.getIdUser(id);
        if (user != null) {
            model.addAttribute("people", user);
            return "edit";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("profile/{id}")
    public String updateProfile(@PathVariable(value = "id") Long id, @ModelAttribute("people") User updatedUser, BindingResult bindingResult, @RequestParam("multipartFiles") MultipartFile[] multipartFiles) throws IOException  {
        User existingUser = service.getIdUser(id);
        MultipartFile multipartFile = updatedUser.getImageFile();
        if (multipartFile != null && !multipartFile.isEmpty()) {
            try {
                if (!multipartFile.getContentType().startsWith("image/")) {
                    bindingResult.rejectValue("imageFile", "error.people", "Неверный тип файла");
                    return "edit";
                }
                long MAX_SIZE = 10 * 1960 * 1960; // 5MB
                if (multipartFile.getSize() > MAX_SIZE) {
                    bindingResult.rejectValue("imageFile", "error.people", "Файл слишком большой");
                    return "edit";
                }

                Image image = new Image();
                service.configureImages(multipartFile, image);

                // Установка Image в People
                existingUser.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
                bindingResult.rejectValue("imageFile", "error.people", "Ошибка при загрузке файла");
                return "edit";
            }
        }
        List<File> files = new ArrayList<>();
        if(multipartFiles != null && multipartFiles.length > 0) {
            for(MultipartFile multipartFile1 : multipartFiles) {
                try {
                    if (!multipartFile1.getContentType().startsWith("application/") && !multipartFile1.getContentType().startsWith("text/")) {
                        bindingResult.rejectValue("file", "error.people", "Неверный тип файла");
                        return "edit";
                    }
                    File file = new File();
                    service.configureFiles(multipartFile1, file);
                    files.add(file);
                }catch (IOException e) {
                    e.printStackTrace();
                    bindingResult.rejectValue("file", "error.people", "Ошибка при загрузке файла");
                    return "edit";
                }
            }
        }
        existingUser.setFiles(files);

        if (bindingResult.hasErrors()) {
            return "edit";
        }
        service.update(existingUser, updatedUser);

        return "redirect:/profile/" + existingUser.getId();
    }

    @PostMapping("profile/delete/{id}")
    public String delete(@PathVariable(value = "id") Long id) {
        User user = service.getIdUser(id);
        service.deleteUser(user);
        return "redirect:/login";
    }

    @PostMapping("profile/{id}/search")
    public String searchProfile(@PathVariable(value = "id") Long id, @RequestParam(required = false, name = "title") String query) {

        if (query != null && !query.trim().isEmpty()) {
            String[] parts = query.trim().split("\\s+");

            if (parts.length == 1) {
               User results = service.searchByNameOrSurname(parts[0]);
                return "redirect:/searchProfile/" + results.getId();
            } else if (parts.length >= 2) {
                String name = parts[0];
                String surname = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
                User results = service.searchByNameAndSurname(name, surname);
                return "redirect:/searchProfile/" + results.getId();
            }
        }
        User nowUser = service.getIdUser(id);
        return "redirect:/profile/" + nowUser.getId();
    }

    @GetMapping("searchProfile/{id}")
    public String foundProfile(@PathVariable(value = "id") Long id, Model model) {
        User user = service.getIdUser(id);
        model.addAttribute("people", user);
        return "searchProfile";
    }
}
