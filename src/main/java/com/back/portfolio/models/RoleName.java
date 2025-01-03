package com.back.portfolio.models;

import org.springframework.security.core.GrantedAuthority;


public enum RoleName implements GrantedAuthority  {
    ROLE_USER, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }

    public void setName(String roleAdmin) {

    }
}
