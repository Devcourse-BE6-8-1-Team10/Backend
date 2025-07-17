package com.back.domain.order.dummy;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Dummy implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        // 👤 Member 더미 삽입
        if (memberRepository.count() == 0) {
            Member member = new Member("test@naver.com", "password123", "홍길동", false);
            memberRepository.save(member);
        }

        // 🛒 Product 더미 삽입
        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                    .productName("아메리카노")
                    .price(3500)
                    .imageUrl("https://example.com/americano.jpg")
                    .category("커피")
                    .description("커피")
                    .orderable(true)
                    .build());

            productRepository.save(Product.builder()
                    .productName("카페라떼")
                    .price(4000)
                    .imageUrl("https://example.com/latte.jpg")
                    .category("커피")
                    .description("라떼")
                    .orderable(true)
                    .build());
        }
    }
}
