package com.vijayshopee.product.service;

import com.vijayshopee.product.dto.UnitRequest;
import com.vijayshopee.product.dto.UnitResponse;
import com.vijayshopee.product.model.Unit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UnitService {
    UnitResponse createUnit(UnitRequest request);

    List<UnitResponse> getAllUnits();

    UnitResponse findUnitById(Long id);

    UnitResponse updateUnit(Unit unit, Long id);
}
