package com.example.inventory_service.service;

import com.example.inventory_service.model.Inventory;
import com.example.inventory_service.repository.InventoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository repo;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }

    // Cacheable for checking stock
    @Cacheable(value = "stock", key = "#id")
    public boolean checkStock(Long id) {
        System.out.println("DB hit for product " + id); // just to test caching
        return repo.findById(id)
                .map(item -> item.getStock() > 0)
                .orElse(false);
    }

    //  Save new stock (evict cache for this id if exists)
    @CacheEvict(value = "stock", key = "#inv.id")
    public Inventory addStock(Inventory inv) {
        return repo.save(inv);
    }

    public List<Inventory> getAll() {
        return repo.findAll();
    }

    public Inventory getOne(Long id) {
        return repo.findById(id).orElse(null);
    }
}
