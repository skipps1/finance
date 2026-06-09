package com.skipps.finance.dto.budget;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record BudgetDTO(
    @NotNull
    long id,

    String category,

    BigDecimal amountLimit
) {}
