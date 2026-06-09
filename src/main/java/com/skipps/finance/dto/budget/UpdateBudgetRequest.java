package com.skipps.finance.dto.budget;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateBudgetRequest(
    Long categoryId,
    @NotNull
    @Positive
    BigDecimal amountLimit
) {
}
