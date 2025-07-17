package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public long count() {
        return memberRepository.count();
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member join(String email, String password, String name) {
        memberRepository
                .findByEmail(email)
                .ifPresent(_member -> {
                    throw new ServiceException(409, "이미 존재하는 이메일입니다.");
                });

        password = passwordEncoder.encode(password);

        Member member = new Member(email, password, name);
        return memberRepository.save(member);
    }

    //관리자로 가입
    public Member joinAdmin(String email, String password, String name) {
        memberRepository
                .findByEmail(email)
                .ifPresent(_member -> {
                    throw new ServiceException(409, "이미 존재하는 이메일입니다.");
                });

        password = passwordEncoder.encode(password);

        Member member = new Member(email, password, name, true);
        return memberRepository.save(member);
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }

    public Map<String, Object> payload(String accessToken) {
        return authTokenService.payload(accessToken);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public void checkPassword(Member member, String password) {
        if (!passwordEncoder.matches(password, member.getPassword()))
            throw new ServiceException(401, "비밀번호가 일치하지 않습니다.");

    }

    public void withdraw(Member member) {
        if (member.isAdmin())
            throw new ServiceException(403, "관리자는 탈퇴할 수 없습니다.");


        memberRepository.delete(member);
    }
}
