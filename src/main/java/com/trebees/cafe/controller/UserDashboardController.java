package com.trebees.cafe.controller;

import com.trebees.cafe.model.*;
import com.trebees.cafe.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    @Autowired private ProductService productService;
    @Autowired private OrderService orderService;
    @Autowired private CustomerAccountService customerService;
    @Autowired private BankAccountService bankAccountService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        CustomerAccount user = customerService.findByEmail(email).orElseThrow();
        double approvedFunds = user.getBankAccounts().stream().filter(t -> t.getStatus().equalsIgnoreCase("APPROVED")).mapToDouble(BankAccount::getMoney).sum();
        List<Product> products = productService.findAll();
        model.addAttribute("user", user);
        model.addAttribute("balance", approvedFunds);
        model.addAttribute("products", products);
        return "user/dashboard";
    }

    @GetMapping("/topup")
    public String topupFormAndHistory(Model model, Authentication authentication) {
        String email = authentication.getName();
        CustomerAccount user = customerService.findByEmail(email).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("newTopup", new BankAccount());
        model.addAttribute("topups", user.getBankAccounts());
        return "user/topup";
    }

    @PostMapping("/topup")
    public String submitTopup(@ModelAttribute("newTopup") @Valid BankAccount topup, BindingResult result, Authentication authentication) {
        if (result.hasErrors()) return "redirect:/user/topup?error";
        String email = authentication.getName();
        CustomerAccount user = customerService.findByEmail(email).orElseThrow();
        topup.setCustomer(user);
        topup.setStatus("PENDING");
        bankAccountService.save(topup);
        return "redirect:/user/topup?topup_submitted";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        CustomerAccount user = customerService.findByEmail(email).orElseThrow();
        List<Order> orders = user.getOrders();
        double totalPendingPayments = orders.stream().filter(o -> o.getStatus().equalsIgnoreCase("Pending Approval")).mapToDouble(Order::getPrice).sum();
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("totalPendingPayments", totalPendingPayments);
        return "user/orders";
    }

    // NEW -- Load Checkout Page
    @GetMapping("/checkout/{productName}")
    public String checkoutPage(@PathVariable String productName, Model model, Authentication authentication) {
        String email = authentication.getName();
        CustomerAccount user = customerService.findByEmail(email).orElseThrow();
        Optional<Product> productOpt = productService.findById(productName);
        if (productOpt.isEmpty()) return "redirect:/user/dashboard?error_product_not_found";
        model.addAttribute("user", user);
        model.addAttribute("product", productOpt.get());
        return "user/checkout";
    }

    // NEW -- Confirm Checkout
    @PostMapping("/checkout/confirm")
    public String confirmCheckout(@RequestParam String productName, @RequestParam int quantity, Authentication authentication) {
        String email = authentication.getName();
        CustomerAccount user = customerService.findByEmail(email).orElseThrow();
        Optional<Product> productOpt = productService.findById(productName);
        if (productOpt.isEmpty() || quantity <= 0) return "redirect:/user/dashboard?error_invalid_checkout";
        Product product = productOpt.get();
        double totalPrice = product.getPrice() * quantity;
        double approvedFunds = user.getBankAccounts().stream().filter(t -> t.getStatus().equalsIgnoreCase("APPROVED")).mapToDouble(BankAccount::getMoney).sum();
        if (approvedFunds < totalPrice) return "redirect:/user/dashboard?insufficient_funds";
        Order order = new Order();
        order.setCustomer(user);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setPrice(totalPrice);
        order.setStatus("Pending Approval");
        order.setOrderDateTime(LocalDateTime.now());
        orderService.save(order);
        return "redirect:/user/orders?checkout_success";
    }
}