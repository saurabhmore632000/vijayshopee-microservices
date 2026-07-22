package com.vijayshopee.inventory.service;

import com.vijayshopee.inventory.dto.InventoryRequest;
import com.vijayshopee.inventory.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    InventoryResponse addStock(InventoryRequest request);

    InventoryResponse updateStockAbsolute(InventoryRequest request);

    void deductStock(Long  productId,Integer quantity);

    List<InventoryResponse> getAllStocks();

    InventoryResponse getStockById(Long productId);
}
