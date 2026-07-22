package com.vijayshopee.product.service;

import com.vijayshopee.product.dto.SubCategoryRequest;
import com.vijayshopee.product.dto.SubCategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SubCategoryService {
    SubCategoryResponse createSubCategory(SubCategoryRequest req);

    List<SubCategoryResponse> getAll();
}
