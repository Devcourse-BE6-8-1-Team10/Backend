package com.back.global.initData;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

/**
 * 개발 환경의 초기 데이터 설정
 */
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevInitData {
    @Autowired
    @Lazy
    private DevInitData self;
    private final MemberService memberService;

    @Bean
    ApplicationRunner devInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    // 유저 데이터 삽입
    @Transactional
    public void work1() {
        if (memberService.count() > 0) return;
        Member memberSystem = memberService.joinAdmin("system@gmail.com", "1234", "시스템");
        memberSystem.modifyApiKey(memberSystem.getEmail());

        Member memberAdmin = memberService.joinAdmin("admin@gmail.com", "1234", "관리자");
        memberAdmin.modifyApiKey(memberAdmin.getEmail());

        Member user1 = memberService.join("user1@gmail.com", "1234", "유저1");
        user1.modifyApiKey(user1.getEmail());

        Member user2 = memberService.join("user2@gmail.com", "1234", "유저2");
        user2.modifyApiKey(user2.getEmail());

        Member user3 = memberService.join("user3@gmail.com", "1234", "유저3");
        user3.modifyApiKey(user3.getEmail());
    }

}
