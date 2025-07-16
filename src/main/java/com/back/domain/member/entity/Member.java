package com.back.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    // ------------ [필드] ------------
    @Id
    @Email

    @Column(length = 150)
    private String email;

    @Column(nullable = false, length = 50)
    private String password;

    @Setter
    @Column(nullable = false, length = 50)
    private String name; // 가변 닉네임

    @Column(nullable = false, unique = true)
    private String apiKey;

    @Column(nullable = false)
    private boolean isAdmin;

    // ------------ [생성자] ------------
    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.apiKey = UUID.randomUUID().toString();
        this.isAdmin = false;
    }

    public Member(String email, String password, String name, boolean isAdmin) {
        this(email, password, name);
        this.isAdmin = isAdmin;
    }

    // ------------ [메서드] ------------
    public boolean isAdmin() {
        return isAdmin;
    }

}
