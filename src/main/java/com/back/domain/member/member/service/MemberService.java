package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;


    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member join(String email, String password, String name) {
        memberRepository
                .findByEmail(email)
                .ifPresent(_member -> {
                    throw new ServiceException(409, "이미 존재하는 이메일입니다.");
                });

        Member member = new Member(email, password, name);
        return memberRepository.save(member);
    }
}
