package com.meridiantrust.sentinel.rule;

import com.meridiantrust.sentinel.model.Alert;
import com.meridiantrust.sentinel.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class HighRiskJurisdictionRule implements DetectionRule {

    private static final Set<String> HIGH_RISK_COUNTRIES = Set.of("KP", "IR", "SY", "CU");

    @Override
    public String getRuleName() {
        return "High-Risk Jurisdiction";
    }

    @Override
    public Optional<Alert> evaluate(Transaction transaction) {
        if (transaction.jurisdiction() != null && HIGH_RISK_COUNTRIES.contains(transaction.jurisdiction().toUpperCase())) {
            Alert alert = new Alert(
                    UUID.randomUUID().toString(),
                    null,
                    transaction.accountId(),
                    List.of(transaction.id()),
                    getRuleName(),
                    100,
                    "Transaction involves a high-risk jurisdiction: " + transaction.jurisdiction(),
                    LocalDateTime.now(),
                    "NEW"
            );
            return Optional.of(alert);
        }
        return Optional.empty();
    }
}
