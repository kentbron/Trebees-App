package com.trebees.cafe.service;

import com.trebees.cafe.model.Checkout;
import com.trebees.cafe.repository.CheckoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckoutService {
    
    @Autowired
    private CheckoutRepository repository;
    
    public List<Checkout> findAll() {
        return repository.findAll();
    }
    
    public Optional<Checkout> findById(Long id) {
        return repository.findById(id);
    }
    
    public Checkout save(Checkout checkout) {
        return repository.save(checkout);
    }
    
    public Checkout update(Long id, Checkout checkoutDetails) {
        return repository.findById(id)
            .map(existing -> {
                existing.setOrder(checkoutDetails.getOrder());
                existing.setProduct(checkoutDetails.getProduct());
                existing.setPrice(checkoutDetails.getPrice());
                return repository.save(existing);
            })
            .orElse(null);
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
