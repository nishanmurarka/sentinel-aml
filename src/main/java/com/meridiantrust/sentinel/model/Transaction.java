package com.meridiantrust.sentinel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transaction {
    @Id
    private String id;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String counterpartyId;
    private String counterpartyName;
    private String channel;
    private LocalDateTime timestamp;
    private String jurisdiction;
    private String direction;

    public Transaction() {}

    public Transaction(String id, String accountId, BigDecimal amount, String currency, String counterpartyId, String counterpartyName, String channel, LocalDateTime timestamp, String jurisdiction, String direction) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.counterpartyId = counterpartyId;
        this.counterpartyName = counterpartyName;
        this.channel = channel;
        this.timestamp = timestamp;
        this.jurisdiction = jurisdiction;
        this.direction = direction;
    }

    public String id() { return id; }
    public String accountId() { return accountId; }
    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }
    public String counterpartyId() { return counterpartyId; }
    public String counterpartyName() { return counterpartyName; }
    public String channel() { return channel; }
    public LocalDateTime timestamp() { return timestamp; }
    public String jurisdiction() { return jurisdiction; }
    public String direction() { return direction; }
}
