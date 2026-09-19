package com.meridiantrust.sentinel.controller;

import com.meridiantrust.sentinel.repository.AlertRepository;
import com.meridiantrust.sentinel.repository.TransactionRepository;
import com.meridiantrust.sentinel.repository.CustomerRepository;
import com.meridiantrust.sentinel.repository.AccountRepository;
import com.meridiantrust.sentinel.service.TransactionProducer;
import com.meridiantrust.sentinel.model.Transaction;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final AlertRepository alertRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionProducer transactionProducer;

    public DashboardController(AlertRepository alertRepository, 
                               TransactionRepository transactionRepository, 
                               CustomerRepository customerRepository, 
                               AccountRepository accountRepository,
                               TransactionProducer transactionProducer) {
        this.alertRepository = alertRepository;
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionProducer = transactionProducer;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalAlerts", alertRepository.findAll().size());
        model.addAttribute("highRiskAlerts", alertRepository.findAll().stream().filter(a -> a.riskScore() >= 80).count());
        model.addAttribute("totalTransactions", transactionRepository.findAll().size());
        model.addAttribute("totalCustomers", customerRepository.findAll().size());
        
        model.addAttribute("recentAlerts", alertRepository.findAll().stream()
                .sorted((a1, a2) -> a2.timestamp().compareTo(a1.timestamp()))
                .limit(10).toList());
        
        return "dashboard";
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {
        model.addAttribute("alerts", alertRepository.findAll().stream()
                .sorted((a1, a2) -> Integer.compare(a2.riskScore(), a1.riskScore()))
                .toList());
        return "alerts";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "customers";
    }

    @GetMapping("/accounts")
    public String accounts(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "accounts";
    }

    @PostMapping("/upload-transactions")
    public String uploadTransactions(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            reader.readNext(); // skip header
            String[] line;
            int count = 0;
            while ((line = reader.readNext()) != null) {
                if (line.length >= 8) {
                    // Append a short random string to the ID so repeated uploads of the same CSV don't just overwrite the same IDs in the HashMap
                    String uniqueId = line[0] + "-" + java.util.UUID.randomUUID().toString().substring(0, 6);
                    Transaction tx = new Transaction(
                            uniqueId, line[1], new BigDecimal(line[2]), line[3],
                            line[4], null, line[5], LocalDateTime.parse(line[6]), line[7], "OUTBOUND"
                    );
                    transactionProducer.sendTransaction(tx);
                    count++;
                }
            }
            redirectAttributes.addFlashAttribute("message", "Successfully ingested " + count + " transactions.");
            
            // Give Kafka consumer a brief moment to process the messages before the dashboard reloads
            // This prevents the "count is wrong until I refresh" issue caused by eventual consistency!
            Thread.sleep(800);
            
        } catch (Exception e) {
            log.error("Failed to process transactions CSV via UI", e);
            redirectAttributes.addFlashAttribute("error", "Failed to process CSV: " + e.getMessage());
        }
        return "redirect:/";
    }
}
