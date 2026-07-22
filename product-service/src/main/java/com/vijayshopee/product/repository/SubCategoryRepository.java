package com.vijayshopee.product.repository;

import com.vijayshopee.product.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {
    boolean existsByName(String name);
}
