package com.skipps.finance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skipps.finance.dto.budget.BudgetDTO;
import com.skipps.finance.dto.budget.CreateBudgetRequest;
import com.skipps.finance.dto.budget.UpdateBudgetRequest;
import com.skipps.finance.exception.ResourceNotFoundException;
import com.skipps.finance.model.BudgetModel;
import com.skipps.finance.model.CategoryModel;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.repository.BudgetRepository;
import com.skipps.finance.repository.CategoryRepository;

@Service
public class BudgetService
{

    private final BudgetRepository budgetRepository;


    private final CategoryRepository categoryRepository;

    BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository)
    {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<BudgetDTO> findAll(UserModel user)
    {
        return budgetRepository.findByUser(user)
                .stream()
                .map(model -> new BudgetDTO(model.getId(), categoryName(model.getCategory()),model.getAmountLimit()))
                .toList();
    }

    public BudgetDTO createBudget(CreateBudgetRequest request, UserModel user)
    {
        BudgetModel budget = new BudgetModel();
        budget.setUser(user);
        budget.setAmountLimit(request.amountLimit());

        if(request.categoryId()!=null)
        {
            CategoryModel category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category was not found"));
            budget.setCategory(category);
        }

        BudgetModel temp = budgetRepository.save(budget);

        return new BudgetDTO(temp.getId(), categoryName(temp.getCategory()), temp.getAmountLimit());
    }

    public void deleteBudget(Long id, UserModel user)
    {
        BudgetModel budget = budgetRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Budget was not found"));
        budgetRepository.delete(budget);
    }

    public BudgetDTO updateBudget(long budgetId, UpdateBudgetRequest request, UserModel user)
    {
        BudgetModel oldBudget = budgetRepository.findByIdAndUser(budgetId, user)
            .orElseThrow(() -> new ResourceNotFoundException("Budget was not found"));

        oldBudget.setAmountLimit(request.amountLimit());

        if(request.categoryId()!=null)
        {
            CategoryModel category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category was not found"));

            oldBudget.setCategory(category);
        }

        BudgetModel temp = budgetRepository.save(oldBudget);

        return new BudgetDTO(temp.getId(), categoryName(temp.getCategory()), temp.getAmountLimit());
    }

    String categoryName(CategoryModel category)
    {
        return category == null ? null : category.getName();
    }
}
