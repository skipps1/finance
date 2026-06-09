package com.skipps.finance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skipps.finance.dto.transaction.CreateTransactionRequest;
import com.skipps.finance.dto.transaction.TransactionDTO;
import com.skipps.finance.dto.transaction.UpdateTransactionRequest;
import com.skipps.finance.exception.ResourceNotFoundException;
import com.skipps.finance.model.CategoryModel;
import com.skipps.finance.model.TransactionModel;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.repository.CategoryRepository;
import com.skipps.finance.repository.TransactionRepository;

@Service
public class TransactionService
{

    private TransactionRepository transactionRepository;


    private CategoryRepository categoryRepository;

    TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository)
    {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public TransactionDTO createTransaction(CreateTransactionRequest request, UserModel user)
    {
        TransactionModel transaction = new TransactionModel();

        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setUser(user);
        transaction.setDescription(request.description());
        transaction.setTimestamp(LocalDateTime.now());

        if(request.categoryId()!=null)
        {
            CategoryModel category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category was not found"));
            transaction.setCategory(category);
        }

        TransactionModel temp = transactionRepository.save(transaction);
        return new TransactionDTO(temp.getId(),temp.getType(), temp.getAmount(), temp.getTimestamp(), categoryName(temp.getCategory()), temp.getDescription());
    }

    public void deleteTransaction(long transactionId, UserModel user)
    {
        TransactionModel transaction = transactionRepository.findByIdAndUser(transactionId, user)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction was not found for current user"));
        transactionRepository.delete(transaction);
    }

    public TransactionDTO updateTransaction(long transactionId, UpdateTransactionRequest request, UserModel user)
    {
        TransactionModel oldTransaction = transactionRepository.findByIdAndUser(transactionId, user)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction was not found"));

        oldTransaction.setType(request.type());
        oldTransaction.setAmount(request.amount());
        oldTransaction.setUser(user);
        oldTransaction.setDescription(request.description());
        oldTransaction.setTimestamp(request.timestamp());

        if(request.categoryId()!=null)
        {
            CategoryModel category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category was not found"));
            oldTransaction.setCategory(category);
        }

        TransactionModel temp = transactionRepository.save(oldTransaction);
        return new TransactionDTO(temp.getId(),temp.getType(), temp.getAmount(), temp.getTimestamp(), categoryName(temp.getCategory()), temp.getDescription());
    }

    public List<TransactionDTO> getTransactions(UserModel user)
    {
        return transactionRepository.findByUser(user)
                .stream()
                .map(model -> new TransactionDTO(model.getId(),model.getType(), model.getAmount(), model.getTimestamp(), categoryName(model.getCategory()), model.getDescription()))
                .toList();
    }

    private String categoryName(CategoryModel category)
    {
        return category == null ? null : category.getName();
    }
}
