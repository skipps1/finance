package com.skipps.finance.dto.transaction;

import java.math.BigDecimal;

import com.skipps.finance.model.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTransactionRequest(
    @NotNull
    TransactionType type,

    @NotNull
    @Positive
    BigDecimal amount,

    Long categoryId,

    String description
) {}
