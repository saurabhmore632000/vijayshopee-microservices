package com.vijayshopee.product.controller;

import com.vijayshopee.product.config.SecurityUtils;
import com.vijayshopee.product.dto.UnitRequest;
import com.vijayshopee.product.dto.UnitResponse;
import com.vijayshopee.product.model.Unit;
import com.vijayshopee.product.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/units")
@RequiredArgsConstructor
public class UnitController {
    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(@RequestBody UnitRequest request,
                                                   @RequestHeader(value = "X-User-Role", required = false)String userRole){
        SecurityUtils.requireRole(userRole, "ROLE_SUPERADMIN");
        UnitResponse res = unitService.createUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAllUnits(){
        List<UnitResponse> allUnits = unitService.getAllUnits();
        return ResponseEntity.status(HttpStatus.OK).body(allUnits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getById(@PathVariable Long id){
        UnitResponse res = unitService.findUnitById(id);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> updateUnit(@RequestBody Unit unit,Long id){
        UnitResponse res = unitService.updateUnit(unit,id);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
