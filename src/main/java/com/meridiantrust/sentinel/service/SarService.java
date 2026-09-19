package com.meridiantrust.sentinel.service;

import com.meridiantrust.sentinel.model.Account;
import com.meridiantrust.sentinel.model.Alert;
import com.meridiantrust.sentinel.model.Customer;
import com.meridiantrust.sentinel.model.Transaction;
import com.meridiantrust.sentinel.repository.AccountRepository;
import com.meridiantrust.sentinel.repository.AlertRepository;
import com.meridiantrust.sentinel.repository.CustomerRepository;
import com.meridiantrust.sentinel.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SarService {

    private final AlertRepository alertRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public SarService(AlertRepository alertRepository,
                      AccountRepository accountRepository,
                      CustomerRepository customerRepository,
                      TransactionRepository transactionRepository) {
        this.alertRepository = alertRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    public String generateSarDraft(String alertId) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isEmpty()) {
            return "Error: Alert with ID " + alertId + " not found.";
        }
        Alert alert = alertOpt.get();

        Optional<Account> accountOpt = accountRepository.findById(alert.accountId());
        Account account = accountOpt.orElse(null);

        Customer customer = null;
        if (account != null) {
            customer = customerRepository.findById(account.customerId()).orElse(null);
        } else if (alert.customerId() != null) {
            customer = customerRepository.findById(alert.customerId()).orElse(null);
        }

        List<Transaction> transactions = alert.transactionIds().stream()
                .map(id -> transactionRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        BigDecimal totalAmount = transactions.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder narrative = new StringBuilder();
        narrative.append("SUSPICIOUS ACTIVITY REPORT (SAR) DRAFT\n");
        narrative.append("======================================\n\n");
        narrative.append("Date of Filing: ").append(java.time.LocalDate.now()).append("\n");
        narrative.append("Reference Alert ID: ").append(alert.id()).append("\n\n");

        narrative.append("1. SUBJECT INFORMATION\n");
        narrative.append("----------------------\n");
        if (customer != null) {
            narrative.append("Customer Name: ").append(customer.name()).append("\n");
            narrative.append("Customer ID: ").append(customer.id()).append("\n");
            narrative.append("Risk Rating at time of alert: ").append(customer.riskRating()).append("\n");
        } else {
            narrative.append("Customer Information: Not available or not linked.\n");
        }
        if (account != null) {
            narrative.append("Account ID: ").append(account.id()).append("\n");
            narrative.append("Account Type: ").append(account.accountType()).append("\n");
        } else {
            narrative.append("Account ID: ").append(alert.accountId() != null ? alert.accountId() : "N/A").append("\n");
        }
        narrative.append("\n");

        narrative.append("2. SUSPICIOUS ACTIVITY SUMMARY\n");
        narrative.append("------------------------------\n");
        narrative.append("An automated alert was generated on ")
                .append(alert.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append(" due to triggering the '").append(alert.triggeredRuleName())
                .append("' rule.\n\n");
        narrative.append("System Description:\n").append(alert.description()).append("\n\n");
        narrative.append("Risk Score assigned: ").append(alert.riskScore()).append(" / 100\n\n");

        narrative.append("3. TRANSACTION DETAILS\n");
        narrative.append("----------------------\n");
        narrative.append("Total number of flagged transactions: ").append(transactions.size()).append("\n");
        if (!transactions.isEmpty()) {
            narrative.append("Total aggregate amount involved: ").append(totalAmount)
                    .append(" ").append(transactions.get(0).currency()).append("\n\n");
            narrative.append("Transactions in scope:\n");
            for (Transaction tx : transactions) {
                narrative.append(" - ID: ").append(tx.id())
                        .append(" | Date: ").append(tx.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                        .append(" | Amount: ").append(tx.amount()).append(" ").append(tx.currency())
                        .append(" | Direction: ").append(tx.direction())
                        .append(" | Counterparty: ").append(tx.counterpartyName() != null ? tx.counterpartyName() : tx.counterpartyId())
                        .append(" | Jurisdiction: ").append(tx.jurisdiction()).append("\n");
            }
        } else {
            narrative.append("No specific transaction details could be resolved from the repository.\n");
        }
        narrative.append("\n");

        narrative.append("4. CONCLUSION\n");
        narrative.append("-------------\n");
        narrative.append("Based on the automated detection, the activity exhibits characteristics consistent with ")
                .append(alert.triggeredRuleName()).append(". ");
        narrative.append("Further investigation by the compliance team is recommended to determine if filing with the regulatory body is required.\n");

        return narrative.toString();
    }
}
