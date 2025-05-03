package com.trebees.cafe.controller;

import com.trebees.cafe.model.Product;
import com.trebees.cafe.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService service;
    
    @GetMapping
    public List<Product> findAll() {
        return service.findAll();
    }
    
    @GetMapping("/{productName}")
    public ResponseEntity<Product> findById(@PathVariable String productName) {
        Optional<Product> product = service.findById(productName);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Product create(@RequestBody Product product) {
        return service.save(product);
    }
    
    @PutMapping("/{productName}")
    public ResponseEntity<Product> update(@PathVariable String productName, @RequestBody Product productDetails) {
        Product updated = service.update(productName, productDetails);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{productName}")
    public ResponseEntity<Void> delete(@PathVariable String productName) {
        service.delete(productName);
        return ResponseEntity.noContent().build();
    }
}
