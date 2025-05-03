package com.trebees.cafe.repository;

import com.trebees.cafe.model.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {
    List<CustomerAccount> findByNameContainingOrEmailContaining(String name, String email);
    Optional<CustomerAccount> findByEmail(String email);
    
    // Custom query for Oracle compatibility
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CustomerAccount c WHERE c.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
