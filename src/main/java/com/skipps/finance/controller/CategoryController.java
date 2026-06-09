package com.skipps.finance.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.finance.model.CategoryModel;
import com.skipps.finance.service.CategoryService;

@RestController
@RequestMapping("/api/category")
public class CategoryController
{
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService)
    {
        this.categoryService=categoryService;
    }

    @GetMapping
    public List<CategoryModel> getCategories()
    {
        return categoryService.getCategories();
    }

    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody String categoryName)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryName));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody String categoryName, @PathVariable long categoryId)
    {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategory(categoryId, categoryName));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long categoryId)
    {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
