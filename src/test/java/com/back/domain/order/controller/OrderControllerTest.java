package com.back.domain.order.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.order.dummy.Dummy;
import com.back.domain.order.entity.Order;
import com.back.domain.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void clearData() {
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("주문 생성")
    void createOrder_success() throws Exception {
        // given
        String json = """
        {
          "customerEmail": "test@exam.com",
          "customerAddress": "서울시 종로구",
          "orderItems": [
            {"productId": 1, "count": 2, "price": 3500},
            {"productId": 2, "count": 1, "price": 3500}
          ]
        }
        """;

        // when
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/orders")
                                .contentType("application/json")
                                .content(json)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201-1"))
                .andExpect(jsonPath("$.message").value("1번 주문 생성됨"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.customerEmail").value("test@exam.com"))
                .andExpect(jsonPath("$.data.customerAddress").value("서울시 종로구"))
                .andExpect(jsonPath("$.data.state").value("ORDERED"))
                .andExpect(jsonPath("$.data.createdDate").exists());
    }

    @Test
    @DisplayName("주문 조회")
    void getOrder_success() throws Exception {
        Member dummyMember = Dummy.getDummyMember("user1@naver.com");

        Order saved = orderRepository.save(
                Order.builder()
                        .customerEmail(dummyMember)
                        .customerAddress("강남구")
                        .state("ORDERED")
                        .build()
        );

        // When
        ResultActions result = mvc.perform(get("/api/v1/orders/" + saved.getId()))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saved.getId()))
                .andExpect(jsonPath("$.data.customerEmail").value("user1@naver.com"));
    }

    @Test
    @DisplayName("주문 생성 실패 - 필수값 누락")
    void createOrder_fail_missing_field() throws Exception {
        Map<String, Object> invalidRequest = Map.of(
                "customerEmail", "", // 누락
                "customerAddress", "",
                "orderItems", List.of()
        );

        mvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }
}
