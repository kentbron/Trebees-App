package com.trebees.cafe.controller;

import com.trebees.cafe.model.Checkout;
import com.trebees.cafe.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkouts")
public class CheckoutController {
    
    @Autowired
    private CheckoutService service;
    
    @GetMapping
    public List<Checkout> findAll() {
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Checkout> findById(@PathVariable Long id) {
        Optional<Checkout> checkout = service.findById(id);
        return checkout.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Checkout create(@RequestBody Checkout checkout) {
        return service.save(checkout);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Checkout> update(@PathVariable Long id, @RequestBody Checkout checkoutDetails) {
        Checkout updated = service.update(id, checkoutDetails);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
