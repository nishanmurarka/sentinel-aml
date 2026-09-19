package com.meridiantrust.sentinel.model;

import java.math.BigDecimal;

public record Edge(String from, String to, String label, BigDecimal value) {}
