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
public class StructuringRule implements DetectionRule {

    private final TransactionRepository transactionRepository;

    public StructuringRule(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    private static final BigDecimal LOWER_BOUND = new BigDecimal("9000");
    private static final BigDecimal UPPER_BOUND = new BigDecimal("9999");
    private static final int STRUCTURING_COUNT_THRESHOLD = 3;

    @Override
    public String getRuleName() {
        return "Structuring/Smurfing";
    }

    @Override
    public Optional<Alert> evaluate(Transaction transaction) {
        if (isWithinBounds(transaction.amount())) {
            LocalDateTime cutoff = transaction.timestamp().minusHours(24);
            List<Transaction> recentTransactions = transactionRepository.findByAccountId(transaction.accountId()).stream()
                    .filter(t -> t.timestamp().isAfter(cutoff) && t.timestamp().isBefore(transaction.timestamp().plusSeconds(1)))
                    .filter(t -> isWithinBounds(t.amount()))
                    .collect(Collectors.toList());

            if (recentTransactions.size() > 0 && recentTransactions.size() % STRUCTURING_COUNT_THRESHOLD == 0) {
                Alert alert = new Alert(
                        UUID.randomUUID().toString(),
                        null,
                        transaction.accountId(),
                        recentTransactions.stream().map(Transaction::id).collect(Collectors.toList()),
                        getRuleName(),
                        95,
                        "Detected " + recentTransactions.size() + " transactions between $9000 and $9999 in 24 hours.",
                        LocalDateTime.now(),
                        "NEW"
                );
                return Optional.of(alert);
            }
        }
        return Optional.empty();
    }

    private boolean isWithinBounds(BigDecimal amount) {
        return amount.compareTo(LOWER_BOUND) >= 0 && amount.compareTo(UPPER_BOUND) <= 0;
    }
}
