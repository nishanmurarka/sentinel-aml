package com.meridiantrust.sentinel.rule;

import com.meridiantrust.sentinel.model.Alert;
import com.meridiantrust.sentinel.model.Transaction;
import com.meridiantrust.sentinel.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DetectionRulesTest {

    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = new TransactionRepository();
    }

    @Test
    void testLargeTransactionRule() {
        LargeTransactionRule rule = new LargeTransactionRule();
        
        // Trigger alert (> 10000)
        Transaction largeTx = new Transaction("TX1", "ACC1", new BigDecimal("15000"), "USD", "CP1", null, "WIRE", LocalDateTime.now(), "US", "OUTBOUND");
        assertTrue(rule.evaluate(largeTx).isPresent());

        // No alert (<= 10000)
        Transaction smallTx = new Transaction("TX2", "ACC1", new BigDecimal("5000"), "USD", "CP1", null, "WIRE", LocalDateTime.now(), "US", "OUTBOUND");
        assertFalse(rule.evaluate(smallTx).isPresent());
    }

    @Test
    void testHighRiskJurisdictionRule() {
        HighRiskJurisdictionRule rule = new HighRiskJurisdictionRule();

        // Trigger alert (KP = North Korea)
        Transaction highRiskTx = new Transaction("TX1", "ACC1", new BigDecimal("500"), "USD", "CP1", null, "WIRE", LocalDateTime.now(), "KP", "OUTBOUND");
        assertTrue(rule.evaluate(highRiskTx).isPresent());

        // No alert (US)
        Transaction lowRiskTx = new Transaction("TX2", "ACC1", new BigDecimal("500"), "USD", "CP1", null, "WIRE", LocalDateTime.now(), "US", "OUTBOUND");
        assertFalse(rule.evaluate(lowRiskTx).isPresent());
    }

    @Test
    void testRoundNumberRule() {
        RoundNumberRule rule = new RoundNumberRule();

        // Trigger alert (modulo 1000 == 0)
        Transaction roundTx = new Transaction("TX1", "ACC1", new BigDecimal("5000"), "USD", "CP1", null, "WIRE", LocalDateTime.now(), "US", "OUTBOUND");
        assertTrue(rule.evaluate(roundTx).isPresent());

        // No alert
        Transaction normalTx = new Transaction("TX3", "ACC1", new BigDecimal("5050.50"), "USD", "CP1", null, "WIRE", LocalDateTime.now(), "US", "OUTBOUND");
        assertFalse(rule.evaluate(normalTx).isPresent());
    }

    @Test
    void testStructuringRule() {
        StructuringRule rule = new StructuringRule(transactionRepository);

        LocalDateTime now = LocalDateTime.now();
        // Current transaction (between 9000 and 9999)
        Transaction currentTx = new Transaction("TX3", "ACC1", new BigDecimal("9500"), "USD", "CP1", null, "WIRE", now, "US", "OUTBOUND");
        
        transactionRepository.save(currentTx); // Must save current transaction for the rule to find it!
        
        // Mock history: Two previous transactions today between 9000 and 9999
        Transaction pastTx1 = new Transaction("TX1", "ACC1", new BigDecimal("9200"), "USD", "CP1", null, "WIRE", now.minusHours(1), "US", "OUTBOUND");
        Transaction pastTx2 = new Transaction("TX2", "ACC1", new BigDecimal("9600"), "USD", "CP1", null, "WIRE", now.minusHours(2), "US", "OUTBOUND");
        
        transactionRepository.save(pastTx1);
        transactionRepository.save(pastTx2);
        // We also need to save the currentTx because the StructuringRule counts `recentTransactions.size() >= 3` from the repository!
        transactionRepository.save(currentTx);



        Optional<Alert> alert = rule.evaluate(currentTx);
        assertTrue(alert.isPresent(), "Alert should be present but was empty!");
        assertTrue(alert.get().triggeredRuleName().contains("Structuring"));
    }

    @Test
    void testRapidMovementRule() {
        RapidMovementRule rule = new RapidMovementRule(transactionRepository);

        // Current transaction (Outbound 4000)
        Transaction outboundTx = new Transaction("TX2", "ACC1", new BigDecimal("4000"), "USD", "CP1", null, "WIRE", LocalDateTime.now(), "US", "OUTBOUND");

        // Mock history: Inbound deposit of 5000 yesterday
        Transaction inboundTx = new Transaction("TX1", "ACC1", new BigDecimal("5000"), "USD", "CP1", null, "WIRE", LocalDateTime.now().minusHours(24), "US", "INBOUND");
        
        transactionRepository.save(inboundTx);

        // Outbound (4000) is 80% of Inbound (5000), should trigger
        Optional<Alert> alert = rule.evaluate(outboundTx);
        assertTrue(alert.isPresent());
        assertTrue(alert.get().description().contains("80%"));
    }
}
