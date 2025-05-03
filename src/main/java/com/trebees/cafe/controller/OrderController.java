package com.trebees.cafe.controller;

import com.trebees.cafe.model.Order;
import com.trebees.cafe.model.Product;
import com.trebees.cafe.service.OrderService;
import com.trebees.cafe.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutOrder(@RequestParam("productName") String productName) {
        // Get the product to check if it is available
        Product product = productService.findById(productName).orElse(null);

        if (product == null || product.getIsAvailable() == 0) {
            // If the product is unavailable, prevent checkout
            return ResponseEntity.badRequest().body("This product is no longer available for checkout.");
        }

        // Proceed with order creation if the product is available
        Order order = new Order();
        order.setProduct(product);
        order.setPrice(product.getPrice());
        order.setStatus("Pending");

        // Save the order
        orderService.save(order);

        return ResponseEntity.ok("Order placed successfully.");
    }
}
