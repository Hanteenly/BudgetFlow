package org.example.budgetflow.service;

import org.example.budgetflow.DTO.BudgetSummary;
import org.example.budgetflow.Repository.AccountRepository;
import org.example.budgetflow.Repository.BudgetRepository;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.entity.Budget;
import org.example.budgetflow.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void getSummary_returnsOverLimitWhenSpentExceedsLimit() {
        Category category = new Category("FOOD");
        YearMonth period = YearMonth.of(2026, 10);

        Budget budget = new Budget(
                category,
                new BigDecimal("200"),
                period
        );

        when(transactionRepository.amountFromMonth(category,
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0)))
                .thenReturn(new BigDecimal("250"));
        BudgetSummary result = budgetService.getSummary(budget);

        assertEquals(new BigDecimal("-50"), result.getRemaining());
        assertEquals(true, result.getOverLimit());
    }

    @Test
    void getSpend_returnsCorrectTotal(){
        YearMonth period = YearMonth.of(2026, 10);
        Category category = new Category("FOOD");
        BigDecimal num = new BigDecimal("200");

        Budget budget = new Budget(
                category,
                num,
                period
        );
        LocalDateTime start = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 11, 1, 0, 0);
        when(transactionRepository.amountFromMonth(
                category,
                start,
                end))
                .thenReturn(num);

        assertEquals(num, budgetService.spent(budget));
    }
}
