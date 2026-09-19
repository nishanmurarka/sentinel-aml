package com.meridiantrust.sentinel.repository;

import com.meridiantrust.sentinel.model.Alert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Repository
public class AlertRepository {
    private final Queue<Alert> alerts = new ConcurrentLinkedQueue<>();

    public Alert save(Alert alert) {
        alerts.add(alert);
        return alert;
    }

    public List<Alert> findAll() {
        return new ArrayList<>(alerts);
    }
    
    public List<Alert> findByCustomerId(String customerId) {
        return alerts.stream()
                .filter(a -> a.customerId() != null && a.customerId().equals(customerId))
                .collect(Collectors.toList());
    }
}
