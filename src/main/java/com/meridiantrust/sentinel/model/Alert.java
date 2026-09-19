package com.meridiantrust.sentinel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Alert {
    @Id
    private String id;
    private String customerId;
    private String accountId;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alert_transaction_ids", joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "transaction_id")
    private List<String> transactionIds;
    
    private String triggeredRuleName;
    private int riskScore;
    private String description;
    private LocalDateTime timestamp;
    private String status;

    public Alert() {}

    public Alert(String id, String customerId, String accountId, List<String> transactionIds, String triggeredRuleName, int riskScore, String description, LocalDateTime timestamp, String status) {
        this.id = id;
        this.customerId = customerId;
        this.accountId = accountId;
        this.transactionIds = transactionIds;
        this.triggeredRuleName = triggeredRuleName;
        this.riskScore = riskScore;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String id() { return id; }
    public String customerId() { return customerId; }
    public String accountId() { return accountId; }
    public List<String> transactionIds() { return transactionIds; }
    public String triggeredRuleName() { return triggeredRuleName; }
    public int riskScore() { return riskScore; }
    public String description() { return description; }
    public LocalDateTime timestamp() { return timestamp; }
    public String status() { return status; }
}
