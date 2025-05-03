package com.trebees.cafe.repository;

import com.trebees.cafe.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /** Count only pending-approval orders (for “mark unavailable” checks). */
    @Query("""
      SELECT COUNT(o)
        FROM Order o
       WHERE o.product.productName = :productName
         AND LOWER(TRIM(o.status)) = 'pending approval'
      """)
    long countPendingOrdersForProduct(@Param("productName") String productName);

    /**
     * Count ALL orders (any status) for a product.
     * Used to block deletion if any order references it.
     */
    @Query("""
      SELECT COUNT(o)
        FROM Order o
       WHERE o.product.productName = :productName
      """)
    long countOrdersForProduct(@Param("productName") String productName);

    /** Find all pending orders by customer (unchanged). */
    @Query("""
      SELECT o
        FROM Order o
       WHERE o.customer.customer_id = :customerId
         AND LOWER(TRIM(o.status)) = 'pending approval'
      """)
    List<Order> findAllPendingOrdersByCustomer(@Param("customerId") Long customerId);
}