package com.back.domain.order.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdmOrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주문 목록 조회")
    @WithUserDetails("admin@gmail.com")
    void t1() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/adm/orders")
        )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AdmOrderController.class))
                .andExpect(handler().methodName("getOrders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].customerEmail").exists());
    }

    @Test
    @DisplayName("주문 목록 조회 - 권한 없음")
    @WithUserDetails("user2@gmail.com")
    void t2() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/adm/orders")
                )
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden());
    }
}
