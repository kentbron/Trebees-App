package com.trebees.cafe.controller;

import com.trebees.cafe.model.CustomerAccount;
import com.trebees.cafe.service.CustomerAccountService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthController {

    private final CustomerAccountService customerService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(CustomerAccountService customerService, 
                         PasswordEncoder passwordEncoder) {
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new CustomerAccount());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerCustomer(@Valid @ModelAttribute("customer") CustomerAccount customer, 
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // Check if email already exists
        if (customerService.existsByEmail(customer.getEmail())) {
            model.addAttribute("error", "Email already registered");
            return "auth/register";
        }
        
        // Hash password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerService.save(customer);
        return "redirect:/login?registered";
    }
}