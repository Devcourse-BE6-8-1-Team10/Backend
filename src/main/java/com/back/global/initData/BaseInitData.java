package com.back.global.initData;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.app.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    @Autowired
    @Lazy
    private BaseInitData self;
    private final MemberService memberService;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    // 유저 데이터 삽입
    // 운영 환경이 아닐 땐, API 키를 이메일로 설정
    @Transactional
    public void work1() {
        if (memberService.count() > 0) return;

        Member memberSystem = memberService.joinAdmin("system@gmail.com", "1234", "시스템");
        if(AppConfig.isNotProd()) memberSystem.modifyApiKey(memberSystem.getEmail());

        Member memberAdmin = memberService.joinAdmin("admin@gmail.com", "1234", "관리자");
        if(AppConfig.isNotProd()) memberAdmin.modifyApiKey(memberAdmin.getEmail());

        Member user1 = memberService.join("user1@gmail.com", "1234", "유저1");
        if(AppConfig.isNotProd()) user1.modifyApiKey(user1.getEmail());

        Member user2 = memberService.join("user2@gmail.com", "1234", "유저2");
        if(AppConfig.isNotProd()) user2.modifyApiKey(user2.getEmail());

        Member user3 = memberService.join("user3@gmail.com", "1234", "유저3");
        if(AppConfig.isNotProd()) user3.modifyApiKey(user3.getEmail());
    }


}
