package com.trebees.cafe.service;

import com.trebees.cafe.model.Product;
import com.trebees.cafe.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private final ProductRepository repository;
    
    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    public List<Product> findAll() {
        return repository.findAll();
    }
    
    public Optional<Product> findById(String productName) {
        return repository.findById(productName);
    }
    
    public Product save(Product product) {
        return repository.save(product);
    }
    
    /**
     * Updates only mutable fields of an existing product.
     * The productName (PK) remains unchanged.
     */
    public Product update(String productName, Product details) {
        return repository.findById(productName)
            .map(existing -> {
                existing.setDescription(details.getDescription());
                existing.setPrice(details.getPrice());
                existing.setIsAvailable(details.getIsAvailable());
                if (details.getImagePath() != null) {
                    existing.setImagePath(details.getImagePath());
                }
                return repository.save(existing);
            })
            .orElse(null);
    }
    
    public void delete(String productName) {
        repository.deleteById(productName);
    }
}