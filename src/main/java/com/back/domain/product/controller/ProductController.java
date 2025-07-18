package com.back.domain.product.controller;

import com.back.domain.product.dto.PageDto;
import com.back.domain.product.dto.ProductDto;
import com.back.domain.product.entity.Product;
import com.back.domain.product.service.ProductService;
import com.back.global.exception.ServiceException;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Validated
@Tag(name = "ProductController", description = "상품 API")
@RequiredArgsConstructor
@RestController
//@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "상품 목록 조회",
            description = "페이징 처리"
    )
    @GetMapping("/products")
    @Transactional(readOnly = true)
    public RsData<PageDto> getItems(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int pageSize
    ) {

        Page<Product> productPage = productService.getItems(page, pageSize);


        if (productPage.isEmpty()) { //목록이없더라도, 200 빈데이터 반환
            PageDto emptyPageDto = new PageDto(productPage);
            return RsData.successOf(emptyPageDto);
        }

        PageDto pageDto = new PageDto(productPage);
        return RsData.successOf(pageDto);
    }

    public record GCSReqBody(@NotBlank String productName,
                             @Positive int price,
                             @NotBlank String category,
                             @NotBlank String description,
                             boolean orderable) {
    }

    @Operation(
            summary = "상품 단건 조회",
            description = "상품 ID기반 상품의 상세 정보 조회"
    )
    @GetMapping("/products/{id}")
    @Transactional(readOnly = true)
    public RsData<ProductDto> getItem(@PathVariable long id) {

        Product product = productService.getItem(id).orElseThrow(
                () -> new ServiceException(404, "없는 상품입니다.")
        );

        return new RsData<>(
                200,
                "%d번 상품을 조회하였습니다.".formatted(id),
                new ProductDto(product)
        );
    }

    @Operation(
            summary = "상품 생성",
            description = "json형식 데이터 + file형식으로 상품을 생성합니다"
    )
    @PostMapping("/products")
    @Transactional
    public RsData<ProductDto> createWithImage(
            @RequestPart("data") @Valid GCSReqBody reqBody,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        Product product = productService.uploadObject(reqBody, file);

        return new RsData<>(
                201,
                "%d번 상품이 생성되었습니다.".formatted(product.getId()),
                new ProductDto(product)
        );
    }

    record ModifyReqBody(@NotBlank String productName,
                         @Positive int price,
                         @NotBlank String category,
                         @NotBlank String description,
                         boolean orderable) {

    }

    @Operation(
            summary = "상품 수정",
            description = """
                    수정할때도 json형식+file 형태의 값을 입력해야함
                    시나리오1: 이미지 수정 없음
                    시나리오2: 새 이미지 업로드
                    """

    )
    @PutMapping("/products/{id}")
    @Transactional
    public RsData<ProductDto> modifywithImage(
            @PathVariable long id,
            @RequestPart("data") @Valid ModifyReqBody reqBody,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        Product product = productService.getItem(id).orElseThrow(
                () -> new ServiceException(404, "존재하지 않는 상품입니다")
        );

        //여기서 이미지바꾸는 로직 넣고
        productService.modifyImage(product, file);

        System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ"+product.getImageUrl());

        //나머지 정보 수정
        productService.modify(product, reqBody.productName(), reqBody.price(), product.getImageUrl(),
                reqBody.category(), reqBody.description(), reqBody.orderable());

        return new RsData<>(
                200,
                "%d번 상품이 수정되었습니다.".formatted(id),
                new ProductDto(product)
        );
    }


    @Operation(
            summary = "상품 삭제",
            description = "일단 상품 삭제"
    )
    @DeleteMapping("/products/{id}")
    @Transactional
    public RsData<Void> delete(@PathVariable long id) {

        Product product = productService.getItem(id).orElseThrow(
                () -> new ServiceException(404, "존재하지 않는 상품입니다.")
        );

        productService.delete(product);

        return new RsData<>(
                200,
                "%d번 상품이 삭제되었습니다.".formatted(id),
                null
        );


    }


}
