package com.meridiantrust.sentinel.rule;

import com.meridiantrust.sentinel.model.Alert;
import com.meridiantrust.sentinel.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RoundNumberRule implements DetectionRule {

    @Override
    public String getRuleName() {
        return "Round Number / Just-Below-Threshold";
    }

    @Override
    public Optional<Alert> evaluate(Transaction transaction) {
        BigDecimal amount = transaction.amount();
        // Check for round thousands (e.g., 5000, 15000) above 1000
        if (amount.compareTo(new BigDecimal("1000")) >= 0) {
            BigDecimal[] result = amount.divideAndRemainder(new BigDecimal("1000"));
            if (result[1].compareTo(BigDecimal.ZERO) == 0) {
                Alert alert = new Alert(
                        UUID.randomUUID().toString(),
                        null,
                        transaction.accountId(),
                        List.of(transaction.id()),
                        getRuleName(),
                        50,
                        "Suspiciously round amount: " + amount,
                        LocalDateTime.now(),
                        "NEW"
                );
                return Optional.of(alert);
            }
        }
        return Optional.empty();
    }
}
