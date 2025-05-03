package com.trebees.cafe.controller;

import com.trebees.cafe.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/bank-accounts")
public class AdminBankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping
    public String viewPendingTopUps(Model model) {
        model.addAttribute("pendingTopUps", bankAccountService.findByStatus("PENDING"));
        return "admin/bank-accounts";
    }

    @PostMapping("/approve/{id}")
    public String approveTopUp(@PathVariable Long id) {
        bankAccountService.updateStatus(id, "APPROVED");
        return "redirect:/admin/bank-accounts";
    }

    @PostMapping("/decline/{id}")
    public String declineTopUp(@PathVariable Long id) {
        bankAccountService.updateStatus(id, "DECLINED");
        return "redirect:/admin/bank-accounts";
    }
}