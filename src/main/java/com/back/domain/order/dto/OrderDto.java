package com.back.domain.order.dto;

import com.back.domain.order.entity.Order;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record OrderDto(
        @NonNull Long id,
        @NonNull String customerEmail,
        @NonNull String customerAddress,
        @NonNull String state,
        @NonNull LocalDateTime createdDate
) {
    public OrderDto(Order order) {
        this(
                order.getId(),
                order.getCustomerEmail(),
                order.getCustomerAddress(),
                order.getState(),
                order.getCreatedDate()
        );
    }
}