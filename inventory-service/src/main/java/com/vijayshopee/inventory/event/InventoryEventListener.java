package com.vijayshopee.inventory.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijayshopee.inventory.dto.InventoryRequest;
import com.vijayshopee.inventory.dto.ProductCreatedEvent;
import com.vijayshopee.inventory.dto.ProductUpdatedEvent;
import com.vijayshopee.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product-events",groupId = "inventory-group")
    public void listenProductCreation(String messagePayload){
        log.info("📥 [KAFKA] Received incoming raw signal string from product-events topic stream...");

        try{
            ProductCreatedEvent event = objectMapper.readValue(messagePayload,ProductCreatedEvent.class);
            log.info("📦 Decoded payload successfully for Product: {} (ID: {})", event.getName(), event.getProductId());

            InventoryRequest request = new InventoryRequest(
                    event.getProductId(),
                    event.getInitialQuantity() // This will initialize with 0 units
            );

            inventoryService.addStock(request);
            log.info("✅ Inventory table row successfully created for Product ID: {}", event.getProductId());

        }catch(Exception e){}

    }

    @KafkaListener(topics = "product-updated-event",groupId = "inventory-group")
    public void listenProductUpdate(String messagePayload){
        try{
            ProductUpdatedEvent event = objectMapper.readValue(messagePayload,ProductUpdatedEvent.class);

            if(event.getQuantity()!=null){
                InventoryRequest request = new InventoryRequest(
                        event.getProductId(),
                        event.getQuantity().intValue()
                );

                inventoryService.updateStockAbsolute(request);
            }else {
                log.info("ℹ️ No quantity changes detected. Database levels remain untouched.");
            }


        }catch (Exception e){
            log.error("❌ Failed to process product update event payload", e);
        }
    }
}
