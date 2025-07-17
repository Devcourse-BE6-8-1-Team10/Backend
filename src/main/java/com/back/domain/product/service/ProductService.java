package com.back.domain.product.service;

import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(String productName, int price, String imageUrl,
                          String category, String description, boolean orderable) {

        if(productName==null || productName.trim().isEmpty()){
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if(price<0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }

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

    //컨트롤러 에서 유효성 검증함 , 여기서 생략
    public Page<Product> getItems(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page-1, pageSize);

        return productRepository.findAll(pageRequest);
    }

    public long count() {
        return productRepository.count();
    }

    public Optional<Product> getItem(long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getLatestItem() {
        return productRepository.findTopByOrderByIdDesc(); //상품 id 기준으로 제일 최근 생성된거
    }

    @Transactional
    public void modify(Product product, String productName, int price, String imageUrl,
                       String category, String description, boolean orderable) {
        product.setProductName(productName);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setDescription(description);
        product.setOrderable(orderable);
    }
    public void delete(Product product) {
        productRepository.delete(product);
    }
}
