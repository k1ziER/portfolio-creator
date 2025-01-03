package com.back.portfolio.configurations;

import com.back.portfolio.dao.Peoples;
import com.back.portfolio.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Peoples userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userService.findByEmail(username);

        if (user != null) {
            Long userId = user.getId();
            String redirectUrl =  request.getContextPath() +"/profile/" + userId;
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("login");
        }
    }
}

