package com.back.domain.product.controller;

import com.back.domain.product.dto.PageDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
import com.back.global.rsData.RsData;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public RsData<PageDto> getItems(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int pageSize
    ) {

        Page<Product> productPage = productService.getItems(page,pageSize);


        if(productPage.isEmpty()) { //목록이없더라도, 200 빈데이터 반환
            PageDto emptyPageDto = new PageDto(productPage);
            return RsData.successOf(emptyPageDto);
        }

       PageDto pageDto = new PageDto(productPage);
        return RsData.successOf(pageDto);
    }

}
