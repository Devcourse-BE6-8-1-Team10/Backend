package com.back.demo.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //현재 Member가 없으므로 주석처리
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customer_email", referencedColumnName = "email")
//    private Member customer;

    private String customerAddress;

    private String state;

    private LocalDateTime orderTime;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

//    public Order(Member customer, String customerAddress, String state) {
//        this.customer = customer;
//        this.customerAddress = customerAddress;
//        this.state = state;
//        this.orderTime = LocalDateTime.now();
//    }

//    public void addOrderItem(OrderItem orderItem) {
//        orderItems.add(orderItem);
//        orderItem.setOrder(this);
//    }
}