package com.vijayshopee.product.service;

import com.vijayshopee.product.dto.CategoryRequest;
import com.vijayshopee.product.dto.CategoryResponse;
import com.vijayshopee.product.model.Category;
import com.vijayshopee.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        // 🛑 1. Validation check
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new RuntimeException("Category name already exists, man!");
        }

        // 🏗️ 2. Map DTO to Entity
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();

        // 💾 3. Save to MySQL via SQLyog
        Category savedCategory = categoryRepository.save(category);

        // 🔄 4. Map saved Entity back to DTO Response
        return mapToCategoryResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        // Convert the list of entities into a list of DTO responses
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    // Helper method for clean entity-to-DTO conversion
    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}