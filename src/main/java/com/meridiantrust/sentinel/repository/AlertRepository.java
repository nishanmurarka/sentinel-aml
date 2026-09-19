package com.meridiantrust.sentinel.repository;

import com.meridiantrust.sentinel.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {
    List<Alert> findByCustomerId(String customerId);
}
