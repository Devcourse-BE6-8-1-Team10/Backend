package com.back.domain.product.service;

import com.back.domain.product.controller.ProductController;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import com.back.global.exception.ServiceException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private String bucketName = "cafe-image-storage-2025";

    public Product uploadObject(ProductController.GCSReqBody reqBody, MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new ServiceException(400,"파일이 첨부되지 않았습니다.");
        }

        String keyFileName = "cafeimagestorage-e894a0d38084.json";
        InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getInputStream());

        // Public URL 만들기
        String imageUrl = "https://storage.googleapis.com/" + bucketName + "/" + fileName;

        return productRepository.save(
                Product.builder()
                        .productName(reqBody.productName())
                        .price(reqBody.price())
                        .imageUrl(imageUrl) // 여기에 넣기
                        .category(reqBody.category())
                        .description(reqBody.description())
                        .orderable(reqBody.orderable())
                        .build());
    }


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
