package com.back.domain.product.controller;

import com.back.domain.product.dto.ProductDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public RsData<List<ProductDto>> getItems() {

        List<Product> products = productService.getItems();


        if(products.isEmpty()) {
            return RsData.failOf(Collections.emptyList());
        }

        return RsData.successOf(
                products
                .stream()
                .map(ProductDto::new)
                .toList());
    }

}
