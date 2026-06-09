package com.skipps.finance.dto.summary;

import java.math.BigDecimal;

public record ExpensesByCategoryResponse(
    String categoryName,
    BigDecimal totalSpent
) {
}
