package com.back.domain.order.controller;

import com.back.domain.order.dto.OrderDto;
import com.back.domain.order.entity.Order;
import com.back.domain.order.service.OrderService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/adm/orders")
@RequiredArgsConstructor
@Tag(name = "AdmOrderController", description = "관리자용 주문 API 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class AdmOrderController {
    private final OrderService orderService;

    @GetMapping("")
    @Operation(summary = "주문 목록 조회")
    public RsData<List<OrderDto>> getOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDto> dtos = orders.stream()
                .map(OrderDto::new)
                .toList();
        return new RsData<>(
                200,
                "주문 조회에 성공했습니다.",
                dtos
        );
    }
}
