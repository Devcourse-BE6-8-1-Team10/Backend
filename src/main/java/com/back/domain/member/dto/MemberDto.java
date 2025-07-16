package com.back.domain.member.dto;

import com.back.domain.member.entity.Member;
import org.springframework.lang.NonNull;

public record MemberDto(
        @NonNull Long id,
        @NonNull String email,
        @NonNull String name
) {
    public MemberDto(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public MemberDto(Member member) {
        this(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
}
