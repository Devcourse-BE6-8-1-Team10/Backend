package com.back.global.initData;

import com.back.domain.member.member.service.MemberService;
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

        };
    }

    @Transactional
    public void work1() {
        if (memberService.count() > 0) return;

        memberService.joinAdmin("system@gmail.com", "1234", "시스템");
        memberService.joinAdmin("admin@gmail.com", "1234", "관리자");
        memberService.join("user1@gmail.com", "1234", "유저1");
        memberService.join("user2@gmail.com", "1234", "유저2");
        memberService.join("user3", "1234", "유저3");
    }



}
