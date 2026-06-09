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

import com.skipps.finance.dto.transaction.CreateTransactionRequest;
import com.skipps.finance.dto.transaction.TransactionDTO;
import com.skipps.finance.dto.transaction.UpdateTransactionRequest;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.service.TransactionService;

import jakarta.validation.Valid;

@RequestMapping("/api/transaction")
@RestController
public class TransactionController
{
	private final TransactionService transactionService;

	TransactionController(TransactionService transactionService)
	{
	    this.transactionService=transactionService;
	}

	@GetMapping
	public List<TransactionDTO> getTransactions(@AuthenticationPrincipal UserModel user)
	{
	    return transactionService.getTransactions(user);
	}

	@PostMapping
    public ResponseEntity<TransactionDTO> makeTransaction(@Valid @RequestBody CreateTransactionRequest request,
	    @AuthenticationPrincipal UserModel user)
	{
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(request, user));
	}

	@PutMapping("/{transactionId}")
	public ResponseEntity<TransactionDTO> editTransaction(@Valid @RequestBody UpdateTransactionRequest request,
	    @PathVariable long transactionId,
	    @AuthenticationPrincipal UserModel user)
	{
	    return ResponseEntity.status(HttpStatus.OK).body(transactionService.updateTransaction(transactionId, request, user));
	}

	@DeleteMapping("/{transactionId}")
	public ResponseEntity<Void> deleteTransaction(@PathVariable long transactionId,
	    @AuthenticationPrincipal UserModel user)
	{
        transactionService.deleteTransaction(transactionId, user);
        return ResponseEntity.noContent().build();
	}
}
