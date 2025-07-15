package com.back.domain.product.controller;

import com.back.domain.product.dto.PageDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public RsData<PageDto> getItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize
    ) {

        Page<Product> productPage = productService.getItems(page,pageSize);


        if(productPage.isEmpty()) {
            return RsData.failOf(null);
        }

       PageDto pageDto = new PageDto(productPage);
        return RsData.successOf(pageDto);
    }

}
