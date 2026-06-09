package com.skipps.finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skipps.finance.model.BudgetModel;
import com.skipps.finance.model.UserModel;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetModel, Long>
{
    List<BudgetModel> findByUser(UserModel user);

    Optional<BudgetModel> findByIdAndUser(Long id, UserModel user);

    void deleteByIdAndUser(Long id, UserModel user);


}
