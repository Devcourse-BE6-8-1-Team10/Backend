package com.back.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private String email;

    @Column(nullable = false, length = 50)
    private String password;

    @Setter
    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String apiKey;

    @Column(nullable = false)
    private boolean isAdmin;

    // ------------ [생성자] ------------
    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.name = nickname;
        this.apiKey = UUID.randomUUID().toString();
        this.isAdmin = false;
    }

    public Member(String email, String password, String nickname, boolean isAdmin) {
        this(email, password, nickname);
        this.isAdmin = isAdmin;
    }

    // ------------ [메서드] ------------
    public boolean isAdmin() {
        return isAdmin;
    }

}
