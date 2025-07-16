package com.back.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SecurityUser extends User {
    @Getter
    private final int id;
    @Getter
    private final String email;

    public SecurityUser(
            int id,
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
