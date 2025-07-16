package com.back.domain.order.dummy;

import com.back.domain.member.member.entity.Member;
import com.back.domain.product.entity.Product;

public class Dummy {

    public static Product getDummyProduct(Long id) {
        return Product.builder()
                .productName("더미커피 #" + id)
                .price(3500)
                .imageUrl("https://dummy.image/" + id)
                .category("테스트")
                .description("더미 상품입니다.")
                .orderable(true)
                .build();
    }

    public static Member getDummyMember(String email) {
        // Member 생성자: Member(String email, String password, String name)
        return new Member(
                email,
                "encrypted-password",     // 테스트용
                "더미사용자"
        );
    }
}
