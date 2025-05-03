package com.trebees.cafe.service;

import com.trebees.cafe.model.CustomerAccount;
import com.trebees.cafe.repository.CustomerAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerAccountService {

    private final CustomerAccountRepository repository;

    @Autowired
    public CustomerAccountService(CustomerAccountRepository repository) {
        this.repository = repository;
    }

    public List<CustomerAccount> findAll() {
        return repository.findAll();
    }

    public Optional<CustomerAccount> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<CustomerAccount> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public CustomerAccount save(CustomerAccount customer) {
        return repository.save(customer);
    }

    public void delete(Long id) {
        Optional<CustomerAccount> customer = repository.findById(id);
        customer.ifPresent(repository::delete);
    }

    public boolean existsByEmail(String email) {
        return repository.findByEmail(email).isPresent();
    }
}
