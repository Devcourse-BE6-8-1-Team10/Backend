package com.back.domain.member.dto;

import com.back.domain.member.entity.Member;

// admin 권한 여부를 포함한 DTO 클래스
public record MemberWithAuthDto(
    Long id,
    String email,
    String name,
    boolean isAdmin
) {
    public MemberWithAuthDto(Long id, String email, String name, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public MemberWithAuthDto(Long id, String email, String name) {
        this(id, email, name, false);
    }

    public MemberWithAuthDto(Member member) {
        this(member.getId(), member.getEmail(), member.getName(), member.isAdmin());
    }
}
