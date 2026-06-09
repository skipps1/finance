package com.skipps.finance.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.finance.dto.summary.ExpensesByCategoryResponse;
import com.skipps.finance.dto.summary.MonthlySummaryResponse;
import com.skipps.finance.exception.BadRequestException;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.service.SummaryService;

@RestController
@RequestMapping("/api/summary")
public class SummaryController
{

    private final SummaryService summaryService;

    SummaryController(SummaryService summaryService)
    {
        this.summaryService = summaryService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
        @AuthenticationPrincipal UserModel user,
        @RequestParam int year,
        @RequestParam int month)
    {
        if(month < 1 || month > 12)
        {
            throw new BadRequestException("Invalid month value");
        }
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return ResponseEntity.status(HttpStatus.OK).body(summaryService.makeMonthlySummary(user, start, end));
    }

    @GetMapping("/income")
    public ResponseEntity<BigDecimal> getIncome(
        @AuthenticationPrincipal UserModel user,
        @RequestParam int year,
        @RequestParam int month)
    {
        if(month < 1 || month > 12)
        {
            throw new BadRequestException("Invalid month value");
        }

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        return ResponseEntity.status(HttpStatus.OK).body(summaryService.incomeForPeriod(user, start, end));
    }

    @GetMapping("/expense")
    public ResponseEntity<BigDecimal> getExpenses(
        @AuthenticationPrincipal UserModel user,
        @RequestParam int year,
        @RequestParam int month)
    {
        if(month < 1 || month > 12)
        {
            throw new BadRequestException("Invalid month value");
        }

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        return ResponseEntity.status(HttpStatus.OK).body(summaryService.expensesForPeriod(user, start, end));
    }

    @GetMapping("/expenses/by-category")
    public List<ExpensesByCategoryResponse> getExpensesByCategory(
        @AuthenticationPrincipal UserModel user,
        @RequestParam int year,
        @RequestParam int month)
    {
        if(month < 1 || month > 12)
        {
            throw new BadRequestException("Invalid month value");
        }

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        return summaryService.expensesByCategoryForPeriod(user, start, end);
    }
}
