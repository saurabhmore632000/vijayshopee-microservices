package com.vijayshopee.inventory.service;

import com.vijayshopee.inventory.dto.InventoryRequest;
import com.vijayshopee.inventory.dto.InventoryResponse;
import com.vijayshopee.inventory.model.Inventory;
import com.vijayshopee.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryResponse addStock(InventoryRequest request) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(request.getProductId());
        Inventory inventory;
        if (optionalInventory.isPresent()) {
            // IF found in database: Use the existing record (restocking flow)
            inventory = optionalInventory.get();
            log.info("🔄 Product ID {} already exists. Preparing to update existing stock.", request.getProductId());
        } else {
            // ELSE: Create a brand new Inventory record from scratch (new product flow)
            log.info("✨ Product ID {} is new. Creating a brand new ledger entry.", request.getProductId());
            inventory = Inventory.builder()
                    .productId(request.getProductId())
                    .quantity(0)
                    .build();
        }

        int updatedQuantity = inventory.getQuantity()+ request.getQuantity();
        inventory.setQuantity(updatedQuantity);

        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToInventoryResponse(savedInventory);
    }

    //old not self healing
//    @Override
//    public InventoryResponse updateStockAbsolute(InventoryRequest request) {
//        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
//                .orElseThrow(()->new RuntimeException("Inventory record not found for product ID"+request.getProductId()));
//
//        inventory.setQuantity(request.getQuantity());
//
//        Inventory savedInventory = inventoryRepository.save(inventory);
//        return mapToInventoryResponse(savedInventory);
//    }


    @Override
    public InventoryResponse updateStockAbsolute(InventoryRequest request) {
    Optional <Inventory> optionalInventory = inventoryRepository.findByProductId(request.getProductId());
    Inventory inventory;

    if(optionalInventory.isPresent()){
        inventory = optionalInventory.get();
    }else {
        inventory = Inventory.builder()
                .productId(request.getProductId())
                .build();
    }

        inventory.setQuantity(request.getQuantity());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToInventoryResponse(savedInventory);
    }

    @Override
    @Transactional
    public void deductStock(Long productId, Integer quantity) {
        // 1. Find the target stock ledger row
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory record not found for product ID " + productId));

        // 2. Safety Check: Verify if you have enough items to fulfill the order
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock levels! Product ID: " + productId +
                    " has " + inventory.getQuantity() + " units, requested deduction: " + quantity);
        }

        // 3. Compute new absolute value
        int updatedQuantity = inventory.getQuantity() - quantity;
        inventory.setQuantity(updatedQuantity);

        // 4. Save changes to MySQL
        inventoryRepository.save(inventory);
    }

    @Override
    public List<InventoryResponse> getAllStocks() {
        List<Inventory> inv = inventoryRepository.findAll();
        return inv.stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryResponse getStockById(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("No inventory record found for product ID " + productId));
        return mapToInventoryResponse(inventory);
    }

    InventoryResponse mapToInventoryResponse(Inventory inventory){
        return InventoryResponse.builder()
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .inStock(inventory.getQuantity()>0)
                .build();
    }
}
