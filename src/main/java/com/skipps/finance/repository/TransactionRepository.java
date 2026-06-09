package com.skipps.finance.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skipps.finance.model.TransactionModel;
import com.skipps.finance.model.TransactionType;
import com.skipps.finance.model.UserModel;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, Long>
{
    interface CategorySpending
    {
        Long getCategoryId();
        String getCategoryName();
        BigDecimal getTotalSpent();
    }

    List<TransactionModel> findByUser(UserModel user);

    Optional<TransactionModel> findByIdAndUser(Long id, UserModel user);

    void deleteByIdAndUser(Long id, UserModel user);

    List<TransactionModel> findByUserAndTimestampGreaterThanEqualAndTimestampLessThan(
        UserModel user,
        LocalDateTime start,
        LocalDateTime end);

    // @Query("""
    //     SELECT COALESCE(SUM(t.amount), 0)
    //     FROM TransactionModel t
    //     WHERE t.user = :user
    //         AND t.type = true
    //         AND t.timestamp >= :start
    //         AND t.timestamp < :end
    //     """)
    // BigDecimal sumIncomeForPeriod(
    //     @Param("user") UserModel user,
    //     @Param("start") LocalDateTime start,
    //     @Param("end") LocalDateTime end);

    // @Query("""
    //     SELECT COALESCE(SUM(t.amount), 0)
    //     FROM TransactionModel t
    //     WHERE t.user = :user
    //         AND t.type = false
    //         AND t.timestamp >= :start
    //         AND t.timestamp < :end
    //     """)
    // BigDecimal sumExpensesForPeriod(
    //     @Param("user") UserModel user,
    //     @Param("start") LocalDateTime start,
    //     @Param("end") LocalDateTime end);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM TransactionModel t
        WHERE t.user = :user
            AND t.type = :type
            AND t.timestamp >= :start
            AND t.timestamp < :end
        """)
    BigDecimal sumForPeriod(
        @Param("user") UserModel user,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("type") TransactionType type);

    @Query("""
        SELECT c.id AS categoryId,
            c.name AS categoryName,
            COALESCE(SUM(t.amount), 0) AS totalSpent
        FROM TransactionModel t
        LEFT JOIN t.category c
        WHERE t.user = :user
            AND t.type = :type
            AND t.timestamp >= :start
            AND t.timestamp < :end
        GROUP BY c.id, c.name
        """)
    List<CategorySpending> sumExpensesByCategoryForPeriod(
        @Param("user") UserModel user,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("type") TransactionType type);

    default TransactionModel saveForUser(TransactionModel transaction, UserModel user)
    {
        transaction.setUser(user);
        return save(transaction);
    }
}
