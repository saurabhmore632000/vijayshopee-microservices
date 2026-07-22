package com.vijayshopee.product.controller;

import com.vijayshopee.product.config.SecurityUtils;
import com.vijayshopee.product.dto.CategoryRequest;
import com.vijayshopee.product.dto.CategoryResponse;
import com.vijayshopee.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 🔒 Only SUPER_ADMIN can create a Category
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryRequest categoryRequest,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        SecurityUtils.requireRole(userRole, "ROLE_SUPERADMIN");

        CategoryResponse response = categoryService.createCategory(categoryRequest);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}