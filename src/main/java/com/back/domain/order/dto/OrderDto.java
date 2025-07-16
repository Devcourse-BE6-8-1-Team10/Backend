package com.back.domain.order.dto;

import com.back.domain.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Schema(description = "주문 정보 DTO")
public record OrderDto(
        @Schema(description = "주문 ID")
        @NonNull Long id,
        @Schema(description = "주문자 이메일")
        @NonNull String customerEmail,
        @Schema(description = "주문 날짜")
        @NonNull LocalDateTime createdDate,
        @Schema(description = "처리 상태")
        @NonNull String state,
        @Schema(description = "주문 주소")
        @NonNull String customerAddress
        ) {
    public OrderDto(Order order) {
        this(
                order.getId(),
                order.getCustomerEmail(),
                order.getCreatedDate(),
                order.getState(),
                order.getCustomerAddress()
        );
    }
}