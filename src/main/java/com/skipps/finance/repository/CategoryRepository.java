package com.skipps.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skipps.finance.model.CategoryModel;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryModel, Long>
{
    CategoryModel findByName(String name);
    Boolean existsByName(String name);
}
