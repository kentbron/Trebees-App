

package com.trebees.cafe.service;

import com.trebees.cafe.model.Order;
import com.trebees.cafe.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return repository.findById(id);
    }

    public Order save(Order order) {
        if (order.getOrderDateTime() == null) {
            order.setOrderDateTime(LocalDateTime.now());
        }
        return repository.save(order);
    }

    public Order update(Long id, Order orderDetails) {
        return repository.findById(id)
            .map(existing -> {
                existing.setCustomer(orderDetails.getCustomer());
                existing.setProduct(orderDetails.getProduct());
                existing.setPrice(orderDetails.getPrice());
                existing.setStatus(orderDetails.getStatus());
                existing.setOrderDateTime(orderDetails.getOrderDateTime());
                return repository.save(existing);
            })
            .orElse(null);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Bulk Approve All Orders for a Customer
    public void approveAllOrdersByCustomer(Long customerId) {
        List<Order> orders = repository.findAllPendingOrdersByCustomer(customerId);
        for (Order order : orders) {
            order.setStatus("Approved");
        }
        repository.saveAll(orders);
    }

    // Bulk Decline All Orders for a Customer
    public void declineAllOrdersByCustomer(Long customerId) {
        List<Order> orders = repository.findAllPendingOrdersByCustomer(customerId);
        for (Order order : orders) {
            order.setStatus("Declined");
        }
        repository.saveAll(orders);
    }

    // ✅ Needed for Admin Bulk Approve Logic
    public List<Order> findAllPendingOrdersByCustomer(Long customerId) {
        return repository.findAllPendingOrdersByCustomer(customerId);
    }
}