package com.back.domain.order.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.domain.order.controller.OrderController.OrderItemCreateReqBody;
import com.back.domain.order.entity.Order;
import com.back.domain.order.entity.OrderItem;
import com.back.domain.order.repository.OrderRepository;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private  final ProductRepository productRepository;

    @Transactional
    public Order createOrder(String customerEmail, String customerAddress, List<OrderItemCreateReqBody> orderItemsReqBodies) {
        Member member = memberRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ServiceException(404, "존재하지 않는 회원입니다."));

        Order order = new Order(member, customerAddress, "ORDERED");

        for (OrderItemCreateReqBody reqBody : orderItemsReqBodies) {
            Product product = productRepository.findById(reqBody.productId())
                    .orElseThrow(() -> new ServiceException(404, "존재하지 않는 상품입니다."));

            if (!product.isOrderable())
                throw new ServiceException(400, "주문 불가능한 상품입니다.");

            OrderItem orderItem = new OrderItem(order, product, reqBody.count(), product.getPrice());
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(404, "해당 주문이 존재하지 않습니다."));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void delete(Long orderId, Member actor) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(404, "해당 주문이 존재하지 않습니다."));

        if (!order.getCustomer().getId().equals(actor.getId())) {
            throw new ServiceException(403, "%d번 주문 삭제 권한이 없습니다.".formatted(order.getId()));
        }

        orderRepository.delete(order);
    }
}
