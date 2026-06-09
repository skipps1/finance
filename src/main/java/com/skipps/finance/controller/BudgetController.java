package com.skipps.finance.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.finance.dto.budget.BudgetDTO;
import com.skipps.finance.dto.budget.CreateBudgetRequest;
import com.skipps.finance.dto.budget.UpdateBudgetRequest;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.service.BudgetService;

import jakarta.validation.Valid;

@RequestMapping("/api/budget")
@RestController
public class BudgetController
{
	private BudgetService budgetService;

	BudgetController(BudgetService budgetService)
	{
	    this.budgetService=budgetService;
	}

	@GetMapping
	public List<BudgetDTO> getBudgets(@AuthenticationPrincipal UserModel user)
	{
	    return budgetService.findAll(user);
	}

	@PostMapping
	public ResponseEntity<BudgetDTO> createBudget(@AuthenticationPrincipal UserModel user,
	    @Valid @RequestBody CreateBudgetRequest request)
	{
	    return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(request, user));
	}

	@PutMapping("/{budgetId}")
	public ResponseEntity<BudgetDTO> editBudget(@AuthenticationPrincipal UserModel user,
	    @PathVariable long budgetId,
		@Valid @RequestBody UpdateBudgetRequest request)
	{
	    return ResponseEntity.status(HttpStatus.OK).body(budgetService.updateBudget(budgetId, request, user));
	}

	@DeleteMapping("/{budgetId}")
	public ResponseEntity<Void> deleteBudget(@AuthenticationPrincipal UserModel user,
	    @PathVariable long budgetId)
	{
        budgetService.deleteBudget(budgetId, user);
        return ResponseEntity.noContent().build();
	}

}
