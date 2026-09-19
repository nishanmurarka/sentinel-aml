package com.meridiantrust.sentinel.controller;

import com.meridiantrust.sentinel.model.Transaction;
import com.meridiantrust.sentinel.service.TransactionProducer;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/ingest")
public class IngestionController {

    private static final Logger log = LoggerFactory.getLogger(IngestionController.class);
    private final TransactionProducer transactionProducer;

    public IngestionController(TransactionProducer transactionProducer) {
        this.transactionProducer = transactionProducer;
    }

    @PostMapping("/transactions/csv")
    public ResponseEntity<String> uploadTransactionsCsv(@RequestParam("file") MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            reader.readNext(); // skip header
            String[] line;
            int count = 0;
            while ((line = reader.readNext()) != null) {
                if (line.length >= 8) {
                    Transaction tx = new Transaction(
                            line[0], // id
                            line[1], // accountId
                            new BigDecimal(line[2]), // amount
                            line[3], // currency
                            line[4], // counterpartyId
                            null, // counterpartyName
                            line[5], // channel
                            LocalDateTime.parse(line[6]), // timestamp
                            line[7], // jurisdiction
                            "OUTBOUND" // direction placeholder
                    );
                    transactionProducer.sendTransaction(tx);
                    count++;
                }
            }
            return ResponseEntity.ok("Successfully ingested " + count + " transactions to Kafka.");
        } catch (Exception e) {
            log.error("Failed to process transactions CSV", e);
            return ResponseEntity.internalServerError().body("Failed to process CSV: " + e.getMessage());
        }
    }
}
