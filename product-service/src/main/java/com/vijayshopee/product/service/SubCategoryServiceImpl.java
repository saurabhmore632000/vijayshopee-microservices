package com.vijayshopee.product.service;

import com.vijayshopee.product.dto.SubCategoryRequest;
import com.vijayshopee.product.dto.SubCategoryResponse;
import com.vijayshopee.product.model.Category;
import com.vijayshopee.product.model.SubCategory;
import com.vijayshopee.product.repository.CategoryRepository;
import com.vijayshopee.product.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService{
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public SubCategoryResponse createSubCategory(SubCategoryRequest req) {
        if(subCategoryRepository.existsByName(req.getName())){
            throw new RuntimeException("SubCategory "+req.getName()+" already exists");
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found with id: " + req.getCategoryId()));

        SubCategory subCategory =SubCategory.builder()
                .name(req.getName())
                .category(category)
                .build();
        SubCategory savedSubCategory =subCategoryRepository.save(subCategory);

        return SubCategoryResponse.builder()
                .categoryName(category.getName())
                .name(subCategory.getName())
                .build();
    }

    @Override
    public List<SubCategoryResponse> getAll() {
        List<SubCategory> subCat = subCategoryRepository.findAll();
        return subCat.stream().map(subCats->mapToResponse(subCats))
                .collect(Collectors.toList());
    }

    SubCategoryResponse mapToResponse(SubCategory subCat){
        return SubCategoryResponse.builder()
                .id(subCat.getId())
                .name(subCat.getName())
                .categoryName(subCat.getCategory().getName())
                .build();
    }
}
