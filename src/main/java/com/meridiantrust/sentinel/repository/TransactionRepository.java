package com.meridiantrust.sentinel.repository;

import com.meridiantrust.sentinel.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository {
    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    public Transaction save(Transaction transaction) {
        transactions.put(transaction.id(), transaction);
        return transaction;
    }

    public List<Transaction> findByAccountId(String accountId) {
        return transactions.values().stream()
                .filter(t -> t.accountId().equals(accountId))
                .sorted((t1, t2) -> t1.timestamp().compareTo(t2.timestamp()))
                .collect(Collectors.toList());
    }

    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactions.get(id));
    }
    
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }
}
