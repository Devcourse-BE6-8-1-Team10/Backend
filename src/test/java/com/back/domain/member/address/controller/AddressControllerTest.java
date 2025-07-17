package com.back.domain.member.address.controller;

import com.back.domain.member.address.Service.AddressService;
import com.back.domain.member.address.entity.Address;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.rq.Rq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AddressControllerTest {
    @Autowired
    private AddressService addressService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private Rq rq;


    @Test
    @DisplayName("주소 등록")
    @WithUserDetails("user1@gmail.com")
    void submitAddress() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/addresses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "서울특별시"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        Member member = memberService.findByEmail("user1@gmail.com")
                .orElseThrow(() -> new IllegalStateException("유저가 존재하지 않습니다."));

        Address address = member.getLastAddress()
                .orElseThrow(() -> new IllegalStateException("주소가 등록되지 않았습니다."));

        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("submitAddress"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("주소가 등록됐습니다."))
                .andExpect(jsonPath("$.data.id").value(address.getId()))
                .andExpect(jsonPath("$.data.content").value(address.getContent()))
                .andExpect(jsonPath("$.data.member.id").value(member.getId()));

        assertThat(address.getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("주소 등록 - 이미 등록된 주소")
    @WithUserDetails("user1@gmail.com")
    void submitAddress_addressConflict() throws Exception {
        Member member = memberService.findByEmail("user1@gmail.com")
                .orElseThrow(() -> new IllegalStateException("유저가 존재하지 않습니다."));

        Address address = addressService.submitAddress(member, "서울특별시");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/addresses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "서울특별시"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("submitAddress"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("이미 동일한 주소가 존재합니다."));

    }

    @Test
    @DisplayName("주소 등록 - 주소가 비어있음")
    @WithUserDetails("user1@gmail.com")
    void submitAddress_addressEmpty() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/addresses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": ""
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AddressController.class))
                .andExpect(handler().methodName("submitAddress"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("content-NotBlank-must not be blank"));
    }
}