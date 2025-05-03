package com.trebees.cafe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "BANKACCOUNT")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_account_seq")
    @SequenceGenerator(name = "bank_account_seq", sequenceName = "bank_account_seq", allocationSize = 1)
    private Long bank_id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerAccount customer;
    
    @Column(nullable = false)
    private Double money;

    // ✅ New: Top-up request status (PENDING, APPROVED, DECLINED)
    @Column(nullable = false)
    private String status = "PENDING";

    // Getters and Setters
    public Long getBank_id() {
        return bank_id;
    }

    public void setBank_id(Long bank_id) {
        this.bank_id = bank_id;
    }

    public CustomerAccount getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerAccount customer) {
        this.customer = customer;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
