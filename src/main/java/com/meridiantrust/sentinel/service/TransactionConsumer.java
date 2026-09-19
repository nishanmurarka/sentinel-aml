package com.meridiantrust.sentinel.service;

import com.meridiantrust.sentinel.model.Transaction;
import com.meridiantrust.sentinel.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);
    private final TransactionRepository transactionRepository;
    private final RuleEngineService ruleEngineService;

    public TransactionConsumer(TransactionRepository transactionRepository, RuleEngineService ruleEngineService) {
        this.transactionRepository = transactionRepository;
        this.ruleEngineService = ruleEngineService;
    }

    @KafkaListener(topics = "transactions-topic", groupId = "sentinel-consumer-group")
    public void consume(Transaction transaction) {
        log.info("Consumed transaction: {}", transaction.id());
        
        // Save to in-memory store
        transactionRepository.save(transaction);
        
        // Evaluate rules
        ruleEngineService.evaluate(transaction);
    }
}
