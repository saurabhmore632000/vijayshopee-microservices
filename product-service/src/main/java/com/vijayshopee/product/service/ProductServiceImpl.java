package com.vijayshopee.product.service;

import com.vijayshopee.product.dto.ProductRequest;
import com.vijayshopee.product.dto.ProductResponse;
import com.vijayshopee.product.event.ProductCreatedEvent;
import com.vijayshopee.product.event.ProductUpdatedEvent;
import com.vijayshopee.product.model.Category;
import com.vijayshopee.product.model.Product;
import com.vijayshopee.product.model.SubCategory;
import com.vijayshopee.product.repository.CategoryRepository;
import com.vijayshopee.product.repository.ProductRepository;
import com.vijayshopee.product.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Override
    public ProductResponse createProduct(ProductRequest req) {
        if(productRepository.existsByName(req.getName())){
            throw new RuntimeException("Product "+req.getName()+" already Exists");
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not  found by id "+req.getCategoryId()));

        SubCategory subCategory = subCategoryRepository.findById(req.getSubCategoryId())
                .orElseThrow(()->new RuntimeException("SubCategory not found by id "+req.getSubCategoryId()));

        Product product =  Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .category(category)
                .subCategory(subCategory)
                .build();

        Product savedProduct = productRepository.save(product);

        try{
            ProductCreatedEvent eventData = ProductCreatedEvent.builder()
                    .productId(savedProduct.getId())
                    .name(savedProduct.getName())
                    .price(savedProduct.getPrice())
                    .initialQuantity(0) // Initialize catalog entry tracking with zero units
                    .build();

            log.info("📤 Publishing ProductCreatedEvent to Kafka broker for ID: {}", savedProduct.getId());

            kafkaTemplate.send("product-events",String.valueOf(savedProduct.getId()),eventData);

            log.info("✅ ProductCreatedEvent successfully broadcasted to topic cluster!");


        }catch(Exception e){

            log.error("❌ Failed to broadcast product initialization lifecycle event packet", e);
        }

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .categoryName(category.getName())
                .subCategoryName(subCategory.getName())
                .build();
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> mapToProductResponse(product))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getById(Long id) {
        Optional<Product> optionalProduct=productRepository.findById(id);
        return optionalProduct.map(p->mapToProductResponse(p)).orElseThrow(()->new RuntimeException("Product not found"));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(ProductRequest productRequest, Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found with id "+id));

        if(productRequest.getName()!=null && !productRequest.getName().equalsIgnoreCase(existingProduct.getName())){
            if(productRepository.existsByNameAndIdNot(productRequest.getName(), id)){
                throw new RuntimeException("Product already exists with name "+productRequest.getName());
            }

            existingProduct.setName(productRequest.getName());
        }

        if(productRequest.getDescription()!=null){
            existingProduct.setDescription(productRequest.getDescription());
        }

        if(productRequest.getPrice()!=null){
            existingProduct.setPrice(productRequest.getPrice());
        }

        if(productRequest.getQuantity()!=null){
            existingProduct.setQuantity(productRequest.getQuantity());
        }

        if(productRequest.getCategoryId()!=null){
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(()->new RuntimeException("CAtegory Not Found With Id "+productRequest.getCategoryId()));
            existingProduct.setCategory(category);
        }

        if (productRequest.getSubCategoryId() != null) {
            SubCategory subCategory = subCategoryRepository.findById(productRequest.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("SubCategory not found with id " + productRequest.getSubCategoryId()));
            existingProduct.setSubCategory(subCategory);
        }

        Product updatedProduct = productRepository.save(existingProduct);


        try{
            ProductUpdatedEvent updatedEvent = ProductUpdatedEvent.builder()
                    .productId(updatedProduct.getId())
                    .name(updatedProduct.getName())
                    .description(updatedProduct.getDescription())
                    .price(updatedProduct.getPrice())
                    .quantity(updatedProduct.getQuantity())
                    .categoryId(updatedProduct.getCategory().getId())
                    .categoryName(updatedProduct.getCategory().getName())
                    .subCategoryId(updatedProduct.getSubCategory().getId())
                    .subCategoryName(updatedProduct.getSubCategory().getName())
                    .build();

            log.info("📤 Publishing comprehensive ProductUpdatedEvent to topic stream for ID: {}", updatedProduct.getId());

            kafkaTemplate.send("product-updated-event",String.valueOf(updatedProduct.getId()),updatedEvent);


        }catch (Exception e){
            log.error("❌ Failed to broadcast product update lifecycle packet", e);
        }
    return mapToProductResponse(updatedProduct);
    }


    ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(product.getCategory().getName())
                .subCategoryName(product.getSubCategory().getName())
                .build();
    }
}
