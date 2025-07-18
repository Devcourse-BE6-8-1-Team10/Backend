package com.back.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static Long savedOrderId;


    private ResultActions performWithPrint(MockHttpServletRequestBuilder builder) throws Exception {
        return mvc.perform(builder).andDo(print());
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(1)
    @DisplayName("1. 주문 생성")
    void t1() throws Exception {
        Map<String, Object> request = Map.of(
                "customerAddress", "서울시 강남구 테헤란로 123",
                "orderItems", List.of(
                        Map.of("productId", 1, "count", 2),
                        Map.of("productId", 2, "count", 1)
                )
        );

        var result = performWithPrint(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.customerEmail").value("user1@gmail.com"))
                .andExpect(jsonPath("$.data.state").value("ORDERED"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        savedOrderId = objectMapper.readTree(response).get("data").get("id").asLong();
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(2)
    @DisplayName("2. 주문 목록 조회")
    void t2() throws Exception {
        Assertions.assertNotNull(savedOrderId, "t1에서 주문이 생성되지 않았습니다.");

        performWithPrint(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(savedOrderId))
                .andExpect(jsonPath("$.data[0].customerEmail").value("user1@gmail.com"));
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(3)
    @DisplayName("3. 주문 상세 조회")
    void t3() throws Exception {
        Assertions.assertNotNull(savedOrderId, "t1에서 주문이 생성되지 않았습니다.");

        performWithPrint(get("/api/orders/{orderId}/detail", savedOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savedOrderId))
                .andExpect(jsonPath("$.data.customerEmail").value("user1@gmail.com"))
                .andExpect(jsonPath("$.data.orderItems").isArray());
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(4)
    @DisplayName("4. 주문 생성 실패 - 없는 상품")
    void t4() throws Exception {

        Map<String, Object> request = Map.of(
                "customerEmail", "user1@gmail.com",
                "customerAddress", "서울시",
                "orderItems", List.of(
                        Map.of("productId", 99999L, "count", 1)
                )
        );

        performWithPrint(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."));
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(5)
    @DisplayName("5. 배송지 변경 성공")
    void t5() throws Exception {
        performWithPrint(put("/api/orders/{orderId}/address", savedOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("newAddress", "서울역"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("%s번 주문 주소가 변경되었습니다.".formatted(savedOrderId)));
    }

    @Test
    @WithUserDetails("user2@gmail.com")
    @Order(6)
    @DisplayName("6. 배송지 변경 실패 - 권한 없음")
    void t6() throws Exception {
        performWithPrint(put("/api/orders/{orderId}/address", savedOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("newAddress", "서울역"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("%s번 주문 주소 변경 권한이 없습니다.".formatted(savedOrderId)));
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(7)
    @DisplayName("7. 배송지 변경 실패 - 빈 주소")
    void t7() throws Exception {
        performWithPrint(put("/api/orders/{orderId}/address", savedOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("newAddress", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(8)
    @DisplayName("8. 배송지 변경 실패 - 주문 없음")
    void t8() throws Exception {
        performWithPrint(put("/api/orders/999999/address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("newAddress", "서울역"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 주문이 존재하지 않습니다."));
    }

    @Test
    @WithUserDetails("user2@gmail.com")
    @Order(9)
    @DisplayName("9. 주문 삭제 실패 - 권한 없음")
    void t9() throws Exception {

        performWithPrint(delete("/api/orders/{orderId}", savedOrderId))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value( "%s번 주문 삭제 권한이 없습니다.".formatted(savedOrderId)));
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(10)
    @DisplayName("10. 주문 취소 - 권한 있음")
    void t10() throws Exception {
        Assertions.assertNotNull(savedOrderId, "t1에서 주문이 생성되지 않았습니다.");

        performWithPrint(delete("/api/orders/{orderId}", savedOrderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(savedOrderId + "번 주문이 취소되었습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(11)
    @DisplayName("11. 주문 취소 후 조회 시 404 확인")
    void t11() throws Exception {
        Assertions.assertNotNull(savedOrderId, "삭제된 주문 ID가 null입니다.");

        performWithPrint(get("/api/orders/{orderId}/detail", savedOrderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 주문이 존재하지 않습니다."));
    }

    @Test
    @WithUserDetails("user1@gmail.com")
    @Order(12)
    @DisplayName("12. 주문 삭제 실패 - 존재하지 않는 주문")
    void t12() throws Exception {
        Long nonExistentOrderId = 999999L;

        performWithPrint(delete("/api/orders/{orderId}", nonExistentOrderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("해당 주문이 존재하지 않습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

}