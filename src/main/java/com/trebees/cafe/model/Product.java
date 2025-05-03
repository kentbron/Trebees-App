package com.trebees.cafe.model;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "PRODUCTS")
public class Product {

    @Id
    @Column(name = "product_name")
    @NotBlank(message = "Product name is required")
    private String productName;

    @OneToMany(mappedBy = "product")
    private List<Order> orders;

    @Column
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    @Column(name = "image_path")
    private String imagePath;

    // Add the new isAvailable field
    @Column(name = "is_available", nullable = false)
    private int isAvailable;  // 1 means available, 0 means not available

    // Getters and Setters for the fields
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(int isAvailable) {
        this.isAvailable = isAvailable;
    }
}
