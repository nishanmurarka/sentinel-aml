package com.meridiantrust.sentinel.repository;

import com.meridiantrust.sentinel.model.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AccountRepository {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account save(Account account) {
        accounts.put(account.id(), account);
        return account;
    }

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }

    public List<Account> findByCustomerId(String customerId) {
        return accounts.values().stream()
                .filter(a -> a.customerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public void saveAll(List<Account> accountList) {
        accountList.forEach(this::save);
    }
}
