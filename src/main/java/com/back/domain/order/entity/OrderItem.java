package com.back.domain.order.entity;

import com.back.domain.product.entity.Product;
import com.back.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private int count;

    private int price;

    public OrderItem(Order order, Product product, int count, int price) {
        this.order = order;
        this.product = product;
        this.count = count;
        this.price = price;

        order.addOrderItem(this);
    }
}
