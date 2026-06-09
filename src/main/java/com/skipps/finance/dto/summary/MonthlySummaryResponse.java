package com.skipps.finance.dto.summary;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryResponse
(
    int year,
    int month,
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal balance,
    List<ExpensesByCategoryResponse> expensesByCategory
)
{
    // public MonthlySummaryResponse(
    //     int year,
    //     int month,
    //     BigDecimal totalIncome,
    //     BigDecimal totalExpenses,
    //     BigDecimal balance,
    //     List<ExpensesByCategoryResponse> expensesByCategory)
    // {
    //     this.year=year;
    //     this.month=month;
    //     this.totalIncome=totalIncome;
    //     this.totalExpenses=totalExpenses;
    //     this.balance=balance;
    //     this.expensesByCategory=expensesByCategory;
    // }
}
