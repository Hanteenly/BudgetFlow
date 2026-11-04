package org.example.budgetflow.Repository;

import org.example.budgetflow.DTO.CategoryExpenseReport;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long id);

    @Query("SELECT COALESCE(SUM(u.amount), 0) " +
            "FROM Transaction u " +
            "WHERE u.category = :category " +
            "AND u.createdAt >= :start " +
            "AND u.createdAt < :end " +
            "AND u.type = 'EXPENSE' " +
            "AND u.account.user = :user"
    )
    BigDecimal amountFromMonth(
        @Param("category") Category category,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("user") User user
    );

    @Query("SELECT COALESCE(SUM(u.amount), 0) " +
            "FROM Transaction u " +
            "WHERE u.type = 'EXPENSE' " +
            "AND u.createdAt >= :start " +
            "AND u.createdAt < :end " +
            "AND u.account.user = :user"
    )
    BigDecimal sumExpense(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("user") User user
    );

    @Query("SELECT COALESCE(SUM(u.amount), 0) " +
            "FROM Transaction u " +
            "WHERE u.type = 'INCOME' " +
            "AND u.createdAt >= :start " +
            "AND u.createdAt < :end " +
            "AND u.account.user = :user"
    )
    BigDecimal sumIncome(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("user") User user
    );

    @Query("SELECT new org.example.budgetflow.DTO.CategoryExpenseReport(" +
            "u.category, SUM(u.amount))" +
            "FROM Transaction u " +
            "WHERE u.createdAt >= :start " +
            "AND u.createdAt < :end " +
            "AND u.account.user = :user " +
            "AND u.category IS NOT NULL " +
            "GROUP BY u.category")
    List<CategoryExpenseReport> getCategoryExpenseReport(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("user") User user
    );

    Optional<Transaction> getTransactionByIdAndAccountUser(Long id, User user);
    List<Transaction> getTransactionsByAccountUser(User user);
}









