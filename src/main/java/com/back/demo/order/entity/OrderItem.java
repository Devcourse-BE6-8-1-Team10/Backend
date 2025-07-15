package com.back.demo.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Menu menu;

    private int count;

    private int price;

    public OrderItem(Order order, Menu menu, int count, int price) {
        this.order = order;
//        this.menu = menu;
        this.count = count;
        this.price = price;
    }
}
