package com.meridiantrust.sentinel.model;

import java.time.LocalDate;

public record Account(
    String id,
    String customerId,
    String accountType,
    String currency,
    LocalDate openingDate,
    String status
) {}
