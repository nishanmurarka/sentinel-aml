package com.meridiantrust.sentinel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account {
    @Id
    private String id;
    private String customerId;
    private String accountType;
    private String currency;
    private LocalDate openingDate;
    private String status;

    public Account() {}

    public Account(String id, String customerId, String accountType, String currency, LocalDate openingDate, String status) {
        this.id = id;
        this.customerId = customerId;
        this.accountType = accountType;
        this.currency = currency;
        this.openingDate = openingDate;
        this.status = status;
    }

    public String id() { return id; }
    public String customerId() { return customerId; }
    public String accountType() { return accountType; }
    public String currency() { return currency; }
    public LocalDate openingDate() { return openingDate; }
    public String status() { return status; }
}
