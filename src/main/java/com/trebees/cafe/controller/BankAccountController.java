package com.trebees.cafe.controller;

import com.trebees.cafe.model.BankAccount;
import com.trebees.cafe.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bank-accounts")
public class BankAccountController {
    
    @Autowired
    private BankAccountService service;
    
    @GetMapping
    public List<BankAccount> findAll() {
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> findById(@PathVariable Long id) {
        Optional<BankAccount> bankAccount = service.findById(id);
        return bankAccount.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public BankAccount create(@RequestBody BankAccount bankAccount) {
        return service.save(bankAccount);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BankAccount> update(@PathVariable Long id, @RequestBody BankAccount bankAccountDetails) {
        BankAccount updated = service.update(id, bankAccountDetails);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
