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
public class LargeTransactionRule implements DetectionRule {

    private static final BigDecimal THRESHOLD = new BigDecimal("10000");

    @Override
    public String getRuleName() {
        return "Large Transaction";
    }

    @Override
    public Optional<Alert> evaluate(Transaction transaction) {
        if (transaction.amount().compareTo(THRESHOLD) >= 0) {
            Alert alert = new Alert(
                    UUID.randomUUID().toString(),
                    null, // customerId
                    transaction.accountId(),
                    List.of(transaction.id()),
                    getRuleName(),
                    80,
                    "Single transaction over $10,000 threshold.",
                    LocalDateTime.now(),
                    "NEW"
            );
            return Optional.of(alert);
        }
        return Optional.empty();
    }
}
