package com.back.domain.order.entity;

import com.back.domain.member.member.entity.Member;
import com.back.global.entity.BaseEntityWithTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
@SuperBuilder
public class Order extends BaseEntityWithTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_email", referencedColumnName = "email")
    private Member customer;

    private String customerAddress;

    private String state;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(Member customer, String customerAddress, String state) {
        this.customer = customer;
        this.customerAddress = customerAddress;
        this.state = state;
        this.orderItems = new ArrayList<>();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void changeCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
}