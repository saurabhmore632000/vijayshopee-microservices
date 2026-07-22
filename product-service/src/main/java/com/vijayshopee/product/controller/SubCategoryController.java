package com.vijayshopee.product.controller;

import com.vijayshopee.product.config.SecurityUtils;
import com.vijayshopee.product.dto.SubCategoryRequest;
import com.vijayshopee.product.dto.SubCategoryResponse;
import com.vijayshopee.product.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/sub-categories")
public class SubCategoryController {
    private final SubCategoryService subCategoryService;

    @PostMapping
    public ResponseEntity<SubCategoryResponse> createSubCategory(@RequestBody SubCategoryRequest req,
                                                                 @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        SecurityUtils.requireRole(userRole, "ROLE_SUPERADMIN");

        SubCategoryResponse res = subCategoryService.createSubCategory(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<List<SubCategoryResponse>> getAllSubCategories(){
        List<SubCategoryResponse> res = subCategoryService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
