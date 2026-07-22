package com.vijayshopee.inventory.controller;

import com.vijayshopee.inventory.config.SecurityUtils;
import com.vijayshopee.inventory.dto.InventoryResponse;
import com.vijayshopee.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PutMapping("/deduct")
    public ResponseEntity<Void> deductStock(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        inventoryService.deductStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllStock(@RequestHeader(value = "X-User-Role", required = false) String userRole){
        SecurityUtils.requireRole(userRole, "ROLE_SUPERADMIN");
        List<InventoryResponse> res = inventoryService.getAllStocks();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getStockById(@PathVariable Long productId){
        InventoryResponse res = inventoryService.getStockById(productId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
