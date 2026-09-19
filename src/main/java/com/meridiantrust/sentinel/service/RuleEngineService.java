package com.meridiantrust.sentinel.service;

import com.meridiantrust.sentinel.model.Alert;
import com.meridiantrust.sentinel.model.Transaction;
import com.meridiantrust.sentinel.repository.AlertRepository;
import com.meridiantrust.sentinel.rule.DetectionRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuleEngineService {

    private static final Logger log = LoggerFactory.getLogger(RuleEngineService.class);
    private final List<DetectionRule> rules;
    private final AlertRepository alertRepository;

    public RuleEngineService(List<DetectionRule> rules, AlertRepository alertRepository) {
        this.rules = rules;
        this.alertRepository = alertRepository;
    }

    public void evaluate(Transaction transaction) {
        log.debug("Evaluating transaction {} against {} rules", transaction.id(), rules.size());
        
        for (DetectionRule rule : rules) {
            try {
                Optional<Alert> alertOpt = rule.evaluate(transaction);
                alertOpt.ifPresent(alert -> {
                    log.warn("ALERT GENERATED: {} for transaction {}", rule.getRuleName(), transaction.id());
                    alertRepository.save(alert);
                });
            } catch (Exception e) {
                log.error("Error evaluating rule {} for transaction {}", rule.getRuleName(), transaction.id(), e);
            }
        }
    }
}
