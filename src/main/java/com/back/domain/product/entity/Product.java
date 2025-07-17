package com.back.domain.product.entity;

import com.back.global.entity.BaseEntityWithTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Product extends BaseEntityWithTime {

    @Column(length = 100, unique = true)
    private String productName;
    private int price;
    @Column(length = 100, unique = true)
    private String imageUrl;
    @Column(length = 30)
    private String category;
    @Column(length = 100)
    private String description;
    private boolean orderable;

}
