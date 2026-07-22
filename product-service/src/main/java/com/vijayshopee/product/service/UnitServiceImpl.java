package com.vijayshopee.product.service;

import com.vijayshopee.product.dto.UnitRequest;
import com.vijayshopee.product.dto.UnitResponse;
import com.vijayshopee.product.model.Unit;
import com.vijayshopee.product.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService{
    private final UnitRepository unitRepository;

    @Override
    public UnitResponse createUnit(UnitRequest request) {

        if (unitRepository.existsByName(request.getName())){
            throw new RuntimeException(request.getName()+" already exists");
        }

        Unit unit = Unit.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Unit savedUnit = unitRepository.save(unit);
        return UnitResponse.builder()
                .id(unit.getId())
                .name(unit.getName())
                .description(unit.getDescription())
                .build();
    }

    @Override
    public List<UnitResponse> getAllUnits() {
        List<Unit> units = unitRepository.findAll();
        return units.stream()
                .map(this::mapToUnitResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UnitResponse findUnitById(Long id) {
        Optional<Unit> optionalUnit=unitRepository.findById(id);
        return optionalUnit.map(u->mapToUnitResponse(u)).orElseThrow(()->new RuntimeException("Unit Not Found"));
    }

    @Override
    public UnitResponse updateUnit(Unit unit, Long id) {
        Unit existingUnit = unitRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Unit not found with id "+id));

            existingUnit.setName(unit.getName());
            existingUnit.setDescription(unit.getDescription());

        Unit updatedUnit = unitRepository.save(existingUnit);
        return mapToUnitResponse(updatedUnit);
    }

    UnitResponse mapToUnitResponse(Unit unit){
        return UnitResponse.builder()
                .id(unit.getId())
                .name(unit.getName())
                .description(unit.getDescription())
                .build();
    }

}
