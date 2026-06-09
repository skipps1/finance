package com.skipps.finance.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skipps.finance.dto.summary.ExpensesByCategoryResponse;
import com.skipps.finance.dto.summary.MonthlySummaryResponse;
import com.skipps.finance.model.TransactionType;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.repository.TransactionRepository;

@Service
public class SummaryService
{

	private TransactionRepository transactionRepository;

	SummaryService(TransactionRepository transactionRepository)
	{
	    this.transactionRepository = transactionRepository;
	}

	public BigDecimal incomeForPeriod(UserModel user, LocalDateTime start, LocalDateTime end)
	{
	    return transactionRepository.sumForPeriod(user, start, end, TransactionType.INCOME);
	}

	public BigDecimal expensesForPeriod(UserModel user, LocalDateTime start, LocalDateTime end)
	{
	    return transactionRepository.sumForPeriod(user, start, end, TransactionType.EXPENSE);
	}

	public List<ExpensesByCategoryResponse> expensesByCategoryForPeriod(UserModel user, LocalDateTime start, LocalDateTime end)
	{
	    return transactionRepository
			.sumExpensesByCategoryForPeriod(user, start, end, TransactionType.EXPENSE)
			.stream()
			.map(row -> new ExpensesByCategoryResponse(
			    row.getCategoryName(),
			    row.getTotalSpent()
			))
			.toList();
	}

	public MonthlySummaryResponse makeMonthlySummary(UserModel user,
	    LocalDateTime start,
		LocalDateTime end)
	{
        BigDecimal totalIncome = incomeForPeriod(user, start, end);

        BigDecimal totalExpenses = expensesForPeriod(user, start, end);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        List<ExpensesByCategoryResponse> expensesByCategory =
            expensesByCategoryForPeriod(user, start, end);

        MonthlySummaryResponse response = new MonthlySummaryResponse(
            start.getYear(),
            start.getMonthValue(),
            totalIncome,
            totalExpenses,
            balance,
            expensesByCategory
        );
        return response;
	}

}
