package com.application.poc.inventory.controller;

import com.application.poc.model.InventoryRequest;
import com.application.poc.model.InventoryResponse;
import com.application.poc.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inventory")
public class InventoryController {

    @Autowired
    private InventoryService service;

    @PostMapping("/deduct")
    public InventoryResponse deduct(@RequestBody final InventoryRequest requestDTO){
        return this.service.deductInventory(requestDTO);
    }

    @PostMapping("/add")
    public void add(@RequestBody final InventoryRequest requestDTO){
        this.service.addInventory(requestDTO);
    }

}
