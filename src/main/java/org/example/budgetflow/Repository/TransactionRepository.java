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
            "AND u.type = 'EXPENSE'")
    BigDecimal amountFromMonth(
        @Param("category") Category category,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(u.amount), 0) " +
            "FROM Transaction u " +
            "WHERE u.type = 'EXPENSE' " +
            "AND u.createdAt >= :start " +
            "AND u.createdAt < :end")
    BigDecimal sumExpense(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(u.amount), 0) " +
            "FROM Transaction u " +
            "WHERE u.type = 'INCOME' " +
            "AND u.createdAt >= :start " +
            "AND u.createdAt < :end")
    BigDecimal sumIncome(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT new org.example.budgetflow.DTO.CategoryExpenseReport(" +
            "u.category, SUM(u.amount))" +
            "FROM Transaction u " +
            "WHERE u.type = 'EXPENSE' " +
            "AND u.createdAt >= :start " +
            "AND u.createdAt < :end " +
            "GROUP BY u.category")
    List<CategoryExpenseReport> getCategoryExpenseReport(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    Optional<Transaction> getTransactionByIdAndAccountUser(Long id, User user);
    List<Transaction> getTransactionsByAccountUser(User user);
}









