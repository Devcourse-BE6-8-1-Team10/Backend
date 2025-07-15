package com.back.domain.order.dto;

import com.back.domain.order.entity.OrderItem;
import org.springframework.lang.NonNull;

public record OrderItemDto(
        @NonNull Long id,
        @NonNull Long orderId,
        @NonNull Long productId,
        @NonNull int count,
        @NonNull int price
) {
    public OrderItemDto(OrderItem orderItem) {
        this(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getProduct().getId(),
                orderItem.getCount(),
                orderItem.getPrice()
        );
    }
}
