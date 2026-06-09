package com.skipps.finance.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.skipps.finance.model.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionDTO(
    @NotNull
    Long id,

    @NotNull
    TransactionType type,

    @Positive
    @NotNull
    BigDecimal amount,

    @NotNull
    LocalDateTime timestamp,

    String category,

    String description
) {
}
