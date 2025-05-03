package com.trebees.cafe.controller;


import com.trebees.cafe.model.BankAccount;
import com.trebees.cafe.model.CustomerAccount;
import com.trebees.cafe.model.Order;
import com.trebees.cafe.service.BankAccountService;
import com.trebees.cafe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired private OrderService orderService;
    @Autowired private BankAccountService bankAccountService;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.findAll();
        Map<Long, List<Order>> groupedOrders = orders.stream()
                .filter(o -> o.getCustomer() != null)
                .collect(Collectors.groupingBy(o -> o.getCustomer().getCustomer_id()));

        model.addAttribute("groupedOrders", groupedOrders);
        return "orders/list";
    }

    @PostMapping("/approve/{id}")
    public String approveOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.findById(id).ifPresent(order -> {
            CustomerAccount user = order.getCustomer();
            double price = order.getPrice();

            List<BankAccount> topups = user.getBankAccounts().stream()
                .filter(t -> "APPROVED".equals(t.getStatus()))
                .sorted((a, b) -> Long.compare(a.getBank_id(), b.getBank_id()))
                .toList();

            //Code by: Rigil Kent P. Payo
            //Improved texts in Controller
            for (BankAccount topup : topups) {
                double available = topup.getMoney();
                if (available >= price) {
                    topup.setMoney(available - price);
                    bankAccountService.save(topup);
                    order.setStatus("Approved");
                    orderService.save(order);
                    redirectAttributes.addFlashAttribute("success", "✅ Order approved. Payment has been successfully processed.");
                    return;
                }
            }

            order.setStatus("Declined");
            orderService.save(order);
            redirectAttributes.addFlashAttribute("error", "⚠️ Order declined. The customer does not have enough funds.");
        });
        return "redirect:/admin/orders";
    }

    @PostMapping("/decline/{id}")
    public String declineOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        orderService.findById(id).ifPresent(order -> {
            order.setStatus("Declined");
            orderService.save(order);
            redirectAttributes.addFlashAttribute("success", "✅ Order has been declined successfully.");
        });
        return "redirect:/admin/orders";
    }

    @GetMapping("/approveAll/{customerId}")
    public String approveAllOrders(@PathVariable Long customerId, RedirectAttributes redirectAttributes) {
        List<Order> pendingOrders = orderService.findAllPendingOrdersByCustomer(customerId);

        if (pendingOrders.isEmpty()) {
        	redirectAttributes.addFlashAttribute("info", "ℹ️ This customer has no pending orders to approve.");
            return "redirect:/admin/orders";
        }

        CustomerAccount user = pendingOrders.get(0).getCustomer();

        List<BankAccount> topups = user.getBankAccounts().stream()
                .filter(t -> "APPROVED".equalsIgnoreCase(t.getStatus()))
                .sorted((a, b) -> Long.compare(a.getBank_id(), b.getBank_id()))
                .toList();

        StringBuilder resultMessage = new StringBuilder();
        int approvedCount = 0;
        int declinedCount = 0;

        for (Order order : pendingOrders) {
            double price = order.getPrice();
            boolean deducted = false;

            for (BankAccount topup : topups) {
                if (topup.getMoney() >= price) {
                    topup.setMoney(topup.getMoney() - price);
                    bankAccountService.save(topup);

                    order.setStatus("Approved");
                    orderService.save(order);

                    deducted = true;
                    approvedCount++;

                    resultMessage.append("✅ Order #")
                            .append(order.getOrder_id())
                            .append(" approved (₱")
                            .append(price)
                            .append(" deducted).<br>");
                    break;
                }
            }

            if (!deducted) {
                order.setStatus("Declined");
                orderService.save(order);

                declinedCount++;
                resultMessage.append("❌ Order #")
                        .append(order.getOrder_id())
                        .append(" declined (Insufficient funds).<br>");
            }
        }

        resultMessage.append("<hr>")
                .append("✔️ Approved Orders: ").append(approvedCount)
                .append("<br>❌ Declined Orders: ").append(declinedCount);

        redirectAttributes.addFlashAttribute("bulkApproveMessage", resultMessage.toString());
        return "redirect:/admin/orders";
    }

    @GetMapping("/declineAll/{customerId}")
    public String declineAllOrders(@PathVariable Long customerId, RedirectAttributes redirectAttributes) {
        orderService.declineAllOrdersByCustomer(customerId);
        redirectAttributes.addFlashAttribute("success", "✅ All pending orders for this customer have been declined.");
        return "redirect:/admin/orders";
    }
}
