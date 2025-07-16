package com.back.domain.order.controller;

import com.back.domain.order.dto.OrderDto;
import com.back.domain.order.entity.Order;
import com.back.domain.order.service.OrderService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "OrderController", description = "주문 관련 API 컨트롤러")
public class OrderController {

    private final OrderService orderService;

    public record OrderCreateReqBody(
            @NotBlank @Email String customerEmail,
            @NotBlank String customerAddress,
            @NotEmpty List<OrderItemCreateReqBody> orderItems
    ) {}

    public record OrderItemCreateReqBody(
            @NotNull Long productId,
            @NotNull int count,
            int price
    ) {}

    @PostMapping
    @Operation(summary = "주문 생성 (더미 상품/멤버 사용)")
    public RsData<OrderDto> createOrder(@Valid @RequestBody OrderCreateReqBody reqBody) {
        Order order = orderService.createOrder(
                reqBody.customerEmail(),
                reqBody.customerAddress(),
                reqBody.orderItems()
        );
        return RsData.of("201-1", "%d번 주문 생성됨".formatted(order.getId()), new OrderDto(order));
    }

    @GetMapping("/{orderId}")
    public RsData<OrderDto> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderEntity(orderId);
        return RsData.of("200", "success", new OrderDto(order));
    }
}
