package com.vijayshopee.product.controller;

import com.vijayshopee.product.config.SecurityUtils;
import com.vijayshopee.product.dto.ProductRequest;
import com.vijayshopee.product.dto.ProductResponse;
import com.vijayshopee.product.model.Product;
import com.vijayshopee.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest req,
                                                         @RequestHeader(value = "X-User-Role", required = false) String userRole){

        SecurityUtils.requireRole(userRole, "ROLE_SUPERADMIN");

        ProductResponse res = productService.createProduct(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        List<ProductResponse> res = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id){
        ProductResponse res = productService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductRequest productRequest,@PathVariable Long id){
        ProductResponse res = productService.updateProduct(productRequest,id);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
