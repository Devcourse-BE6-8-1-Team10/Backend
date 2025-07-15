package com.back.domain.order.entity;

import com.back.global.entity.BaseEntity;
import com.back.global.entity.BaseEntityWithTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class Order extends BaseEntityWithTime {

    //현재 Member가 없으므로 주석처리
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_email", referencedColumnName = "email")
//    private Member customer;

    private String customerAddress;

    private String state;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

//    public Order(Member customer, String customerAddress, String state) {
//        this.customer = customer;
//        this.customerAddress = customerAddress;
//        this.state = state;
//    }

//    public void addOrderItem(OrderItem orderItem) {
//        orderItems.add(orderItem);
//        orderItem.setOrder(this);
//    }
}