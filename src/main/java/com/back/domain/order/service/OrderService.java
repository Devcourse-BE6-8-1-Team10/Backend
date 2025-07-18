package com.back.domain.order.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.order.dto.OrderItemParam;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.domain.order.dto.OrderItemCreateReqBody;
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
    public Order createOrder(Member actor, String customerAddress, List<OrderItemParam> OrderItemParam) {

        Order order = new Order(actor, customerAddress, "ORDERED");

        for (OrderItemParam param : OrderItemParam) {
            Product product = productRepository.findById(param.productId())
                    .orElseThrow(() -> new ServiceException(404, "존재하지 않는 상품입니다."));

            if (!product.isOrderable())
                throw new ServiceException(400, "주문 불가능한 상품입니다.");

            OrderItem orderItem = new OrderItem(order, product, param.count(), product.getPrice());
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

    @Transactional
    public void updateOrderAddress(Long orderId, String newAddress, Member actor) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(404, "해당 주문이 존재하지 않습니다."));

        if (!order.getCustomer().getId().equals(actor.getId())) {
            throw new ServiceException(403, "%d번 주문 주소 변경 권한이 없습니다.".formatted(order.getId()));
        }

        order.changeCustomerAddress(newAddress);
        orderRepository.save(order);
    }
}
