package com.back.domain.order.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.order.controller.OrderController.OrderItemCreateReqBody;
import com.back.domain.order.dummy.Dummy;
import com.back.domain.order.entity.Order;
import com.back.domain.order.entity.OrderItem;
import com.back.domain.order.repository.OrderRepository;
import com.back.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(String customerEmail, String customerAddress, List<OrderItemCreateReqBody> orderItemsReqBodies) {

        Member member = Dummy.getDummyMember(customerEmail);

        Order order = Order.builder()
                .customerEmail(member)
                .customerAddress(customerAddress)
                .state("ORDERED")
                .build();

        for (OrderItemCreateReqBody reqBody : orderItemsReqBodies) {
            Product product = Dummy.getDummyProduct(reqBody.productId());
            int price = product.getPrice();
            int count = reqBody.count();

            OrderItem orderItem = new OrderItem(order, product, count, price);
            order.getOrderItems().add(orderItem);
        }

        return orderRepository.save(order);
    }

    public Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
    }
}
