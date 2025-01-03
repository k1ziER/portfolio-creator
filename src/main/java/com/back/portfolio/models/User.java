package com.back.portfolio.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class User implements UserDetails  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @Column(name="password")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String password;

    @NotBlank(message = "Фамилия пользователя не может быть пустой")
    @Column(name = "surname")
    private String surname;

    @Column(name = "description")
    private String description;

    @Column(name = "email")
    @Email(message = "Введите корректный email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotBlank(message = "Телефон не может быть пустым")
    @Column(name = "phone")
    private String phone;

    @Column(name = "active")
    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private Set<Role> roles = new HashSet<>();

    private LocalDateTime dateCreated;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private  List<File> files = new ArrayList<>();

    @Transient
    private MultipartFile imageFile;

    @Transient
    private MultipartFile file;

    @PrePersist
    public void init(){
        this.dateCreated = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        log.debug("Проверка, активен ли пользователь: {}", isActive);
        return isActive;
    }
    public boolean isAdmin(User user) {
        return user.getRoles().stream()
            .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
    }
}
