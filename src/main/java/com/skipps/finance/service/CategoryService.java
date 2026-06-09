package com.skipps.finance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skipps.finance.exception.ResourceNotFoundException;
import com.skipps.finance.model.CategoryModel;
import com.skipps.finance.repository.CategoryRepository;

@Service
public class CategoryService
{
	private CategoryRepository categoryRepository;

	CategoryService(CategoryRepository categoryRepository)
	{
	    this.categoryRepository = categoryRepository;
	}

	//CRUD
	public String createCategory(String categoryName)
	{
        CategoryModel category = new CategoryModel(categoryName);
	    return categoryRepository.save(category).getName();
	}

	public List<CategoryModel> getCategories()
	{
	    return categoryRepository.findAll();
	}

	public String updateCategory(long categoryId, String newCategoryName)
	{
        CategoryModel oldCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category was not found"));

        oldCategory.setName(newCategoryName);

        return categoryRepository.save(oldCategory).getName();
	}

	public void deleteCategory(long categoryId)
	{
        CategoryModel category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category was not found"));
        categoryRepository.delete(category);
	}
}
