package com.meridiantrust.sentinel.model;

import java.time.LocalDateTime;
import java.util.List;

public record Alert(
    String id,
    String customerId,
    String accountId,
    List<String> transactionIds,
    String triggeredRuleName,
    int riskScore,
    String description,
    LocalDateTime timestamp,
    String status
) {}
