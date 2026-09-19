package com.meridiantrust.sentinel.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(
    String id,
    String accountId,
    BigDecimal amount,
    String currency,
    String counterpartyId,
    String counterpartyName,
    String channel,
    LocalDateTime timestamp,
    String jurisdiction,
    String direction
) {}
