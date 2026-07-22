package com.vijayshopee.product.repository;

import com.vijayshopee.product.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit,Long> {
    boolean existsByName (String name);
}
