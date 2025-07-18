package com.back.domain.order.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.order.dto.OrderDto;
import com.back.domain.order.dto.OrderDtoWithSpecific;
import com.back.domain.order.dto.OrderItemParam;
import com.back.domain.order.entity.Order;
import com.back.domain.order.service.OrderService;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "OrderController", description = "주문 API 컨트롤러")
public class OrderController {

    private final OrderService orderService;
    private final Rq rq;

    public record OrderCreateReqBody(
            @NotBlank String customerAddress,
            @NotEmpty List<OrderItemCreateReqBody> orderItems
    ) {
    }

    public record OrderItemCreateReqBody(
            @NotNull Long productId,
            @NotNull int count
    ) {
        public OrderItemParam toParam() {
            return new OrderItemParam(productId, count);
        }
    }

    @PostMapping
    @Operation(summary = "주문 생성")
    public RsData<OrderDto> createOrder(@Valid @RequestBody OrderCreateReqBody reqBody) {
        Member actor = rq.getActor();
        List<OrderItemParam> orderItemParams = reqBody.orderItems()
                .stream()
                .map(OrderItemCreateReqBody::toParam)
                .toList();
        Order order = orderService.createOrder(
                actor,
                reqBody.customerAddress(),
                orderItemParams
        );
        return new RsData<>(
                201,
                "%s번 주문이 생성되었습니다.".formatted(order.getId()),
                new OrderDto(order)
        );
    }

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

    // 주문 상세 조회
    @GetMapping("/{orderId}/detail")
    @Operation(summary = "주문 상세 조회")
    public RsData<OrderDtoWithSpecific> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderService.getOrderEntity(orderId);
        return new RsData<>(
                200,
                "주문 상세 조회에 성공했습니다.",
                new OrderDtoWithSpecific(order));
    }

    //주문 취소
    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 취소")
    public RsData<Void> cancelOrder(@PathVariable Long orderId) {
        Member actor = rq.getActor();
        orderService.cancelOrder(orderId, actor);
        return new RsData<>(
                200,
                "%d번 주문이 취소되었습니다.".formatted(orderId),
                null
        );
    }

    public record OrderUpdateAddressReqBody(
            @NotBlank String newAddress
    ) {
    }

    @PutMapping("/{orderId}/address")
    @Operation(summary = "주문 주소 변경")
    public RsData<Void> updateOrderAddress(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateAddressReqBody reqBody
    ) {
        Member actor = rq.getActor();
        orderService.updateOrderAddress(orderId, reqBody.newAddress(), actor);
        return new RsData<>(
                200,
                "%s번 주문 주소가 변경되었습니다.".formatted(orderId),
                null
        );
    }
}
