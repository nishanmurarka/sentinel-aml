package com.meridiantrust.sentinel.rule;

import com.meridiantrust.sentinel.model.Alert;
import com.meridiantrust.sentinel.model.Transaction;

import java.util.Optional;

public interface DetectionRule {
    String getRuleName();
    Optional<Alert> evaluate(Transaction transaction);
}
