package com.trebees.cafe.service;

import com.trebees.cafe.model.BankAccount;
import com.trebees.cafe.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {
    
    @Autowired
    private BankAccountRepository repository;
    
    public List<BankAccount> findAll() {
        return repository.findAll();
    }

    public List<BankAccount> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Optional<BankAccount> findById(Long id) {
        return repository.findById(id);
    }

    public BankAccount save(BankAccount bankAccount) {
        return repository.save(bankAccount);
    }

    public BankAccount update(Long id, BankAccount bankAccountDetails) {
        return repository.findById(id)
            .map(existing -> {
                existing.setCustomer(bankAccountDetails.getCustomer());
                existing.setMoney(bankAccountDetails.getMoney());
                existing.setStatus(bankAccountDetails.getStatus()); // new
                return repository.save(existing);
            })
            .orElse(null);
    }

    public void updateStatus(Long id, String status) {
        repository.findById(id).ifPresent(account -> {
            account.setStatus(status);
            repository.save(account);
        });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
