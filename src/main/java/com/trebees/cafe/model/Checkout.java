package com.trebees.cafe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CHECKOUTS")
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checkout_seq")
    @SequenceGenerator(name = "checkout_seq", sequenceName = "checkout_seq", allocationSize = 1)
    private Long checkout_id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "product_name", nullable = false)
    private Product product;
    
    private Double price;

    // Getters and Setters
    public Long getCheckout_id() {
        return checkout_id;
    }

    public void setCheckout_id(Long checkout_id) {
        this.checkout_id = checkout_id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
