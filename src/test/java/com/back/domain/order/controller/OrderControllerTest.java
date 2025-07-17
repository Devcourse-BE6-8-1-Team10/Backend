package com.back.domain.order.controller;

import com.back.domain.member.member.service.MemberService;
import com.back.domain.order.repository.OrderRepository;
import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberService memberService;
    @Autowired private ProductService productService;
    @Autowired private OrderRepository orderRepository;

    private static Long savedOrderId;
    private Product p1, p2;

    @BeforeEach
    @Transactional
    void setUp() {
        // 회원 생성
        if (memberService.findByEmail("user1@gmail.com").isEmpty()) {
            memberService.join("user1@gmail.com", "1234", "유저1");
        }

        // 중복 방지 UUID 포함
        String uuid1 = UUID.randomUUID().toString().substring(0, 8);
        String uuid2 = UUID.randomUUID().toString().substring(0, 8);

        p1 = productService.create("커피" + uuid1, 3500, null, "음료", "아메", true);
        p2 = productService.create("치즈" + uuid2, 4000, null, "디저트", "케이크", true);
    }

    @Test
    @WithMockUser("user1@gmail.com")
    @Order(1)
    @DisplayName("1. 주문 생성 성공")
    void t1_createOrder() throws Exception {
        Map<String, Object> request = Map.of(
                "customerEmail", "user1@gmail.com",
                "customerAddress", "서울 강남구",
                "orderItems", List.of(
                        Map.of("productId", p1.getId(), "count", 2),
                        Map.of("productId", p2.getId(), "count", 1)
                )
        );

        var result = mvc.perform(post("/api/orders")
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
    @WithMockUser("user1@gmail.com")
    @Order(2)
    @DisplayName("2. 주문 목록 조회")
    void t2_getOrders() throws Exception {
        Assertions.assertNotNull(savedOrderId, "t1_createOrder에서 주문이 생성되지 않았습니다.");

        mvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(savedOrderId))
                .andExpect(jsonPath("$.data[0].customerEmail").value("user1@gmail.com"));
    }

    @Test
    @WithMockUser("user1@gmail.com")
    @Order(3)
    @DisplayName("3. 주문 상세 조회")
    void t3_getOrderDetail() throws Exception {
        Assertions.assertNotNull(savedOrderId, "t1_createOrder에서 주문이 생성되지 않았습니다.");

        mvc.perform(get("/api/orders/{orderId}/detail", savedOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savedOrderId))
                .andExpect(jsonPath("$.data.customerEmail").value("user1@gmail.com"))
                .andExpect(jsonPath("$.data.orderItems").isArray());
    }

    @Test
    @WithMockUser("user1@gmail.com")
    @Order(4)
    @DisplayName("4. 주문 생성 실패 - 없는 회원")
    void t4_createOrder_fail_no_member() throws Exception {
        Map<String, Object> request = Map.of(
                "customerEmail", "noone@email.com",
                "customerAddress", "서울시",
                "orderItems", List.of(
                        Map.of("productId", p1.getId(), "count", 1)
                )
        );

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
    }

    @Test
    @WithMockUser("user1@gmail.com")
    @Order(5)
    @DisplayName("5. 주문 생성 실패 - 없는 상품")
    void t5_createOrder_fail_no_product() throws Exception {
        if (memberService.findByEmail("test2@email.com").isEmpty()) {
            memberService.join("test2@email.com", "1234", "임꺽정");
        }

        Map<String, Object> request = Map.of(
                "customerEmail", "test2@email.com",
                "customerAddress", "서울시",
                "orderItems", List.of(
                        Map.of("productId", 99999L, "count", 1)
                )
        );

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 상품입니다."));
    }

    @Test
    @WithMockUser("user1@gmail.com")
    @Order(6)
    @DisplayName("6. 주문 취소")
    void t6_deleteOrderTest() throws Exception {
        Assertions.assertNotNull(savedOrderId, "t1_createOrder에서 주문이 생성되지 않았습니다.");

        mvc.perform(delete("/api/orders/{orderId}", savedOrderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(savedOrderId + "번 주문이 취소되었습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithMockUser("user1@gmail.com")
    @Order(7)
    @DisplayName("7. 주문 취소 후 상세 조회 시 404 확인")
    void t7_deletedOrderShouldNotBeFound() throws Exception {
        Assertions.assertNotNull(savedOrderId, "삭제된 주문 ID가 null입니다.");

        mvc.perform(get("/api/orders/{orderId}/detail", savedOrderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 주문이 존재하지 않습니다."));
    }

    @Test
    @WithMockUser("user1@gmail.com")
    @Order(8)
    @DisplayName("8. 주문 삭제 실패 - 존재하지 않는 주문")
    void t8_deleteOrder_fail_not_found() throws Exception {
        Long nonExistentOrderId = 999999L;

        mvc.perform(delete("/api/orders/{orderId}", nonExistentOrderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("존재하지 않는 주문입니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}