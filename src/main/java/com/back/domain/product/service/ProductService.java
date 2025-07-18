package com.back.domain.product.service;

import com.back.domain.product.controller.ProductController;
import com.back.domain.product.entity.Product;
import com.back.domain.product.repository.ProductRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    @Value("${custom.gcp.bucket}")
    private String bucketName;

    public String imageUpload(MultipartFile file) throws IOException {

        String keyFileName = "cafeimagestorage-e894a0d38084.json";
        InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        String fileName = file.getOriginalFilename();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getInputStream());

        // Public URL 만들기
        String imageUrl = "https://storage.googleapis.com/" + bucketName + "/" + fileName;

        return imageUrl;
    }

    public Product uploadObject(ProductController.GCSReqBody reqBody, MultipartFile file) throws IOException {

        // 1. 우선 상품을 imageUrl 없이 저장
        Product product = create(
                reqBody.productName(),
                reqBody.price(),
                "", // 이미지 URL은 비워둔다
                reqBody.category(),
                reqBody.description(),
                reqBody.orderable()
        );

        // 2. 파일이 비어있으면 바로 반환
        if (file == null || file.isEmpty()) {
            return product;
        }

        String keyFileName = "cafeimagestorage-e894a0d38084.json";
        InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        String fileName = product.getId() + file.getOriginalFilename();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getInputStream());

        // Public URL 만들기
        String imageUrl = "https://storage.googleapis.com/" + bucketName + "/" +  fileName;

        product.setImageUrl(imageUrl);

        return productRepository.save(product);
    }


    public Product create(String productName, int price, String imageUrl,
                          String category, String description, boolean orderable) {

        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (price < 0) {
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
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);

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
    public void modifyImage(Product product, MultipartFile file) throws IOException {

        if (file != null && !file.isEmpty()) { //새로 파일 업로드하면, 새 url반환
            String targetUrl = imageUpload(file);
            product.setImageUrl(targetUrl);
        }
        //없으면 기존 이미지 유지

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
