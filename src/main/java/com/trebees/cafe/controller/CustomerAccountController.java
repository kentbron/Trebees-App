// src/main/java/com/trebees/cafe/controller/CustomerAccountController.java

package com.trebees.cafe.controller;

import com.trebees.cafe.model.CustomerAccount;
import com.trebees.cafe.service.CustomerAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerAccountController {

    private final CustomerAccountService customerService;

    @Autowired
    public CustomerAccountController(CustomerAccountService customerService) {
        this.customerService = customerService;
    }

    // List all customers
    @GetMapping
    public String list(Model model,
                       @RequestParam(value="error", required=false) String error) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("error", error);
        return "customers/list";
    }

    // Show add or edit form
    @GetMapping({"/add", "/edit/{id}"})
    public String form(@PathVariable(required=false) Long id, Model model) {
        if (id != null) {
            Optional<CustomerAccount> opt = customerService.findById(id);
            if (opt.isEmpty()) {
                return "redirect:/customers?error=not_found";
            }
            CustomerAccount existing = opt.get();
            if (existing.isAdmin()) {
                return "redirect:/customers?error=admin_edit_not_allowed";
            }
            model.addAttribute("customer", existing);
        } else {
            model.addAttribute("customer", new CustomerAccount());
        }
        return "customers/form";
    }

    // Handle create or update
    @PostMapping("/save")
    public String save(@ModelAttribute("customer") CustomerAccount incoming,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "customers/form";
        }

        if (incoming.getCustomer_id() != null) {
            // --- UPDATE path ---
            CustomerAccount existing = customerService.findById(incoming.getCustomer_id())
                                                     .orElseThrow();
            // Name and address always updatable
            existing.setName(incoming.getName());
            existing.setAddress(incoming.getAddress());

            // Password: only if provided
            if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) {
                existing.setPassword(incoming.getPassword());
            }

            // **Do NOT** change email, isAdmin flag, or bankAccounts list
            customerService.save(existing);
        } else {
            // --- CREATE path ---
            customerService.save(incoming);
        }

        return "redirect:/customers";
    }

    // Delete customer
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Optional<CustomerAccount> opt = customerService.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/customers?error=not_found";
        }
        CustomerAccount cust = opt.get();
        if (cust.isAdmin()) {
            return "redirect:/customers?error=admin_delete_not_allowed";
        }
        customerService.delete(id);
        return "redirect:/customers";
    }
}
