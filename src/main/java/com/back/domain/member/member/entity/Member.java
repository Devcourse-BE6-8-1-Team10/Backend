package com.back.domain.member.member.entity;

import com.back.domain.member.address.entity.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    // ------------ [필드] ------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Setter(AccessLevel.PRIVATE)
    @EqualsAndHashCode.Include
    private Long id;

    @Email
    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50, unique = true)
    private String name; // 가변 닉네임

    @Column(nullable = false, unique = true)
    private String apiKey;

    @Column(nullable = false)
    private boolean isAdmin;

    @OneToMany(
        mappedBy = "member",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Address> addresses = new ArrayList<>();


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

    // new 로 생성하는 경우.
    public Member(Long id, String email, String name){
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");

        this.id = (long) id;
        this.email = email;
        this.name = name;
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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private List<String> getAuthoritiesAsStringList() {
        List<String> authorities = new ArrayList<>();

        if (isAdmin())
            authorities.add("ROLE_ADMIN");

        return authorities;
    }

    public void modifyApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Optional<Address> getLastAddress() {
        if (addresses.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(addresses.get(addresses.size() - 1));
    }

}
