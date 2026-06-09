package com.skipps.finance.dto.budget;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBudgetRequest(

    Long categoryId,
    @Positive
    @NotNull
    BigDecimal amountLimit
) {
}
