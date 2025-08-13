package com.example.inventory_service.controller;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/check/{id}")
    public boolean checkStock(@PathVariable Long id) {
        return service.checkStock(id);
    }

    @PostMapping
    public Inventory addStock(@RequestBody Inventory inv) {
        return service.addStock(inv);
    }

    @GetMapping
    public List<Inventory> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Inventory getOne(@PathVariable Long id) {
        return service.getOne(id);
    }
}
