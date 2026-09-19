package com.meridiantrust.sentinel.service;

import com.meridiantrust.sentinel.model.Account;
import com.meridiantrust.sentinel.model.Customer;
import com.meridiantrust.sentinel.repository.AccountRepository;
import com.meridiantrust.sentinel.repository.CustomerRepository;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataInitializerService {

    private static final Logger log = LoggerFactory.getLogger(DataInitializerService.class);
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public DataInitializerService(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @PostConstruct
    public void initData() {
        log.info("Starting initial data ingestion from CSV...");
        loadCustomers();
        loadAccounts();
        log.info("Initial data ingestion completed.");
    }

    private void loadCustomers() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource("data/customers.csv").getInputStream()))) {
            reader.readNext(); // skip header
            String[] line;
            List<Customer> customers = new ArrayList<>();
            while ((line = reader.readNext()) != null) {
                if (line.length >= 4) {
                    customers.add(new Customer(
                            line[0],
                            line[1],
                            line[2],
                            line[3]
                    ));
                }
            }
            customerRepository.saveAll(customers);
            log.info("Loaded {} customers.", customers.size());
        } catch (Exception e) {
            log.error("Failed to load customers from CSV", e);
        }
    }

    private void loadAccounts() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource("data/accounts.csv").getInputStream()))) {
            reader.readNext(); // skip header
            String[] line;
            List<Account> accounts = new ArrayList<>();
            while ((line = reader.readNext()) != null) {
                if (line.length >= 6) {
                    accounts.add(new Account(
                            line[0],
                            line[1],
                            line[2],
                            line[3],
                            LocalDate.parse(line[4]),
                            line[5]
                    ));
                }
            }
            accountRepository.saveAll(accounts);
            log.info("Loaded {} accounts.", accounts.size());
        } catch (Exception e) {
            log.error("Failed to load accounts from CSV", e);
        }
    }
}
