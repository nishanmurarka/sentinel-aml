package com.meridiantrust.sentinel.repository;

import com.meridiantrust.sentinel.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByCustomerId(String customerId);
}
