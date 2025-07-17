package com.back.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SecurityUser extends User {
    @Getter
    private final Long id;
    @Getter
    private final String email;

    public SecurityUser(
            Long id,
            String email,
            String name,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(name, password, authorities);
        this.id = id;
        this.email = email;
    }
}
