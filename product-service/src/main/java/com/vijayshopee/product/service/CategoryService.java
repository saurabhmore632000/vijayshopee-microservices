package com.vijayshopee.product.service;

import com.vijayshopee.product.dto.CategoryRequest;
import com.vijayshopee.product.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> getAllCategories();
}