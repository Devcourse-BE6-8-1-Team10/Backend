package com.back.domain.order.dto;

import com.back.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;


public record OrderDtoWithSpecific(
        Long id,
        String customerEmail,
        String customerAddress,
        String state,
        LocalDateTime createdDate,
        List<OrderItemDto> orderItems
) {
    // 주문 상세 조회 (아이템 포함)
    public OrderDtoWithSpecific(Order order) {
        this(
                order.getId(),
                order.getCustomer().getEmail(),
                order.getCustomerAddress(),
                order.getState(),
                order.getCreatedDate(),
                order.getOrderItems().stream().map(OrderItemDto::new).toList()
        );
    }
}
