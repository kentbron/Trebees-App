package com.trebees.cafe.service;

import com.trebees.cafe.model.CustomerAccount;
import com.trebees.cafe.repository.CustomerAccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminInitializer {

    private final CustomerAccountRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${trebees.admin.email}")
    private String adminEmail;
    
    @Value("${trebees.admin.password}")
    private String adminPassword;

    public AdminInitializer(CustomerAccountRepository customerRepository, 
                          PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initAdmin() {
        if (!customerRepository.existsByEmail(adminEmail)) {
            CustomerAccount admin = new CustomerAccount();
            admin.setName("System Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setAdmin(true);
            customerRepository.save(admin);
            System.out.println("Admin account created successfully");
        }
    }
}
