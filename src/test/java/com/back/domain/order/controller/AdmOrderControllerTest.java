package com.back.domain.order.controller;

import com.back.domain.order.entity.Order;
import com.back.domain.order.service.OrderService;
import org.hamcrest.Matchers;
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

import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("주문 목록 조회 - 관리자")
    @WithUserDetails("admin@gmail.com")
    void t1() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(get("/api/adm/orders"))
                .andDo(print());

        List<Order> orders = orderService.getAllOrders();

        resultActions
                .andExpect(handler().handlerType(AdmOrderController.class))
                .andExpect(handler().methodName("getOrders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("주문 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(orders.size()));

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            resultActions
                    .andExpect(jsonPath("$.data[%d].id".formatted(i)).value(order.getId()))
                    .andExpect(jsonPath("$.data[%d].customerEmail".formatted(i)).value(order.getCustomer().getEmail()))
                    .andExpect(jsonPath("$.data[0].createdDate").value(Matchers.startsWith(order.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                    .andExpect(jsonPath("$.data[%d].state".formatted(i)).value(order.getStatus().name()))
                    .andExpect(jsonPath("$.data[%d].customerAddress".formatted(i)).value(order.getCustomerAddress()));
        }
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

    @Test
    @DisplayName("주문 상세 조회 - 관리자")
    @WithUserDetails("admin@gmail.com")
    void t3() throws Exception {

        List<Order> orders = orderService.getAllOrders();
        Order targetOrder = orders.get(0);


        ResultActions resultActions = mockMvc
                .perform(get("/api/adm/orders/" + targetOrder.getId() + "/detail"))
                .andDo(print());


        resultActions
                .andExpect(handler().handlerType(AdmOrderController.class))
                .andExpect(handler().methodName("getOrderDetail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("주문 상세 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.id").value(targetOrder.getId()))
                .andExpect(jsonPath("$.data.customerEmail").value(targetOrder.getCustomer().getEmail()))
                .andExpect(jsonPath("$.data.customerAddress").value(targetOrder.getCustomerAddress()))
                .andExpect(jsonPath("$.data.state").value(targetOrder.getStatus().name()))
                .andExpect(jsonPath("$.data.orderItems").isArray());
    }

    @Test
    @DisplayName("주문 상세 조회 - 권한 없음")
    @WithUserDetails("user2@gmail.com")
    void t4() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/adm/orders/1/detail")
                )
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden());
    }
}
