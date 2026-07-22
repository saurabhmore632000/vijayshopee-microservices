package com.vijayshopee.product.repository;

import com.vijayshopee.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name,Long id);
}
