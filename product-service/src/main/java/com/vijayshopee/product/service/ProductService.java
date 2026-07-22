package com.vijayshopee.product.service;

import com.vijayshopee.product.dto.ProductRequest;
import com.vijayshopee.product.dto.ProductResponse;
import com.vijayshopee.product.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    ProductResponse createProduct(ProductRequest req);

    List<ProductResponse> getAllProducts();

    ProductResponse getById(Long id);


    ProductResponse updateProduct(ProductRequest productRequest, Long id);
}
