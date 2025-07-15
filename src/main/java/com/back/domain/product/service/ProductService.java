package com.back.domain.product.service;

import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(String productName, int price, String imageUrl,
                          String category, String description, boolean orderable) {
        return productRepository.save(
                Product
                        .builder()
                        .productName(productName)
                        .price(price)
                        .imageUrl(imageUrl)
                        .category(category)
                        .description(description)
                        .orderable(orderable)
                        .build()
        );

    }

    public Page<Product> getItems(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page-1, pageSize);


        return productRepository.findAll(pageRequest);
    }

    public long count() {
        return productRepository.count();
    }
}
