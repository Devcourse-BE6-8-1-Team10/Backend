package com.back.domain.member.member.entity;

import com.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    // ------------ [필드] ------------
    @Email
    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 50, unique = true)
    private String name; // 가변 닉네임

    @Column(nullable = false, unique = true)
    private String apiKey;

    @Column(nullable = false)
    private boolean isAdmin;

    // ------------ [생성자] ------------
    public Member(String email, String password, String name) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");

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

    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty())
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        this.name = newName;
    }
}
