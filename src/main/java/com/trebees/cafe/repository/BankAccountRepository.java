package com.trebees.cafe.repository;

import com.trebees.cafe.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    //  find top-ups by status
    List<BankAccount> findByStatus(String status);
}
