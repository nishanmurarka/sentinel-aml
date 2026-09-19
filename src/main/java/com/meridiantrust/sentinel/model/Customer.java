package com.meridiantrust.sentinel.model;

public record Customer(
    String id,
    String name,
    String riskRating,
    String kycStatus
) {}
