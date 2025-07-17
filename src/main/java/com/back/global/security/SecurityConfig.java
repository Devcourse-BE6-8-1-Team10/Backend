package com.back.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/favicon.ico").permitAll() // 파비콘 접근 허용 (검색 엔진 최적화)
                .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
                .requestMatchers("/api/members/login", "/api/members/logout").permitAll() // 로그인, 로그아웃은 인증 없이 허용
                .requestMatchers(HttpMethod.POST, "/api/members/join").permitAll() // 회원 가입은 인증 없이 허용
                .requestMatchers("/api/adm/**").hasRole("ADMIN") // 관리자 API는 ADMIN 권한이 있는 사용자만 접근 허용
                .requestMatchers("/api/**").authenticated() // 나머지 API는 인증된 사용자만 접근 허용
                .anyRequest().permitAll()
            )
                .csrf(csrf -> csrf.disable());

        return http.build();

    }

}
