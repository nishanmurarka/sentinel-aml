package com.meridiantrust.sentinel.rule;

import com.meridiantrust.sentinel.model.Alert;
import com.meridiantrust.sentinel.model.Transaction;
import com.meridiantrust.sentinel.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RapidMovementRule implements DetectionRule {

    private final TransactionRepository transactionRepository;

    public RapidMovementRule(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public String getRuleName() {
        return "Rapid Movement of Funds";
    }

    @Override
    public Optional<Alert> evaluate(Transaction transaction) {
        // Only evaluate on outbound transactions
        if ("OUTBOUND".equalsIgnoreCase(transaction.direction())) {
            LocalDateTime cutoff = transaction.timestamp().minusHours(48);
            
            // Find recent deposits (INBOUND)
            List<Transaction> recentDeposits = transactionRepository.findByAccountIdOrderByTimestampAsc(transaction.accountId()).stream()
                    .filter(t -> t.timestamp().isAfter(cutoff) && t.timestamp().isBefore(transaction.timestamp()))
                    .filter(t -> "INBOUND".equalsIgnoreCase(t.direction()))
                    .collect(Collectors.toList());

            BigDecimal totalDeposits = recentDeposits.stream()
                    .map(Transaction::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalDeposits.compareTo(BigDecimal.ZERO) > 0) {
                // If the current outbound transaction is >= 80% of recent deposits
                BigDecimal percentage = transaction.amount().divide(totalDeposits, 2, java.math.RoundingMode.HALF_UP);
                if (percentage.compareTo(new BigDecimal("0.80")) >= 0) {
                    Alert alert = new Alert(
                            UUID.randomUUID().toString(),
                            null,
                            transaction.accountId(),
                            List.of(transaction.id()),
                            getRuleName(),
                            90,
                            "Outbound transfer of " + transaction.amount() + " is >= 80% of deposits in last 48h.",
                            LocalDateTime.now(),
                            "NEW"
                    );
                    return Optional.of(alert);
                }
            }
        }
        return Optional.empty();
    }
}
