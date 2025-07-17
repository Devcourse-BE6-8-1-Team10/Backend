package com.back.domain.member.member.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("회원 가입")
    void join() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "testuser@gmail.com",
                                            "password": "testpassword",
                                            "name": "테스트 유저"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        Member member = memberService.findByEmail("testuser@gmail.com").orElseThrow(() -> new ServiceException(404, "회원이 존재하지 않습니다."));

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName())))
                .andExpect(jsonPath("$.data.id").value(member.getId()))
                .andExpect(jsonPath("$.data.email").value(member.getEmail()))
                .andExpect(jsonPath("$.data.name").value(member.getName()));
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "system@gmail.com",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        Member member = memberService.findByEmail("system@gmail.com").orElseThrow(() -> new ServiceException(404, "회원이 존재하지 않습니다."));

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("%s님 환영합니다.".formatted(member.getName())))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.member.id").value(member.getId()))
                .andExpect(jsonPath("$.data.member.email").value(member.getEmail()))
                .andExpect(jsonPath("$.data.member.name").value(member.getName()))
                .andExpect(jsonPath("$.data.member.isAdmin").value(member.isAdmin()))
                .andExpect(jsonPath("$.data.apiKey").value(member.getApiKey()))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());

        resultActions.andExpect(
                result -> {
                    Cookie apiKeyCookie = result.getResponse().getCookie("apiKey");
                    assertThat(apiKeyCookie.getValue()).isEqualTo(member.getApiKey());
                    assertThat(apiKeyCookie.getPath()).isEqualTo("/");
                    assertThat(apiKeyCookie.getAttribute("HttpOnly")).isEqualTo("true");

                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");
                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.getAttribute("HttpOnly")).isEqualTo("true");
                }
        );
    }

    @Test
    @DisplayName("로그인 - 잘못된 비밀번호")
    void login_wrongPassword() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "system@gmail.com",
                                            "password": "wrong_password"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("로그인 - 잘못된 이메일")
    void login_wrongEmail() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "wrong_system@gmail.com",
                                            "password": "wrong_password"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("존재하지 않는 이메일입니다."));
    }
}