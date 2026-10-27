package org.example.budgetflow.service;

import org.example.budgetflow.DTO.CategoryExpenseReport;
import org.example.budgetflow.DTO.MonthlyReport;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.Repository.UserRepository;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportService reportService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "hash");
        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void getMonthlyReport_returnsCorrectTotals() {
        when(transactionRepository.sumIncome(
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0),
                user
        )).thenReturn(new BigDecimal("300.00"));

        when(transactionRepository.sumExpense(
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0),
                user
        )).thenReturn(new BigDecimal("120.00"));

        MonthlyReport report = reportService.getMonthlyReport("testuser", 2026, 10);

        assertEquals(0, new BigDecimal("300.00").compareTo(report.getIncome()));
        assertEquals(0, new BigDecimal("120.00").compareTo(report.getExpense()));
        assertEquals(0, new BigDecimal("180.00").compareTo(report.getNet()));
    }

    @Test
    void getMonthlyReport_returnsZeroTotalsWhenNoTransactions() {
        when(transactionRepository.sumIncome(
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0),
                user
        )).thenReturn(new BigDecimal("0"));

        when(transactionRepository.sumExpense(
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0),
                user
        )).thenReturn(new BigDecimal("0"));

        MonthlyReport report = reportService.getMonthlyReport("testuser", 2026, 10);

        assertEquals(0, new BigDecimal("0").compareTo(report.getExpense()));
        assertEquals(0, new BigDecimal("0").compareTo(report.getIncome()));
        assertEquals(0, new BigDecimal("0").compareTo(report.getNet()));
    }

    @Test
    void getExpensesByCategory_returnsCategoryTotals() {
        CategoryExpenseReport c1 = new CategoryExpenseReport(new Category("FOOD"), new BigDecimal("100"));
        CategoryExpenseReport c2 = new CategoryExpenseReport(new Category("TRANSPORT"), new BigDecimal("40"));
        List<CategoryExpenseReport> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        when(transactionRepository.getCategoryExpenseReport(
                LocalDateTime.of(2026, 10, 1, 0, 0),
                LocalDateTime.of(2026, 11, 1, 0, 0),
                user
        )).thenReturn(list);

        assertEquals(list, reportService.getExpensesByCategory("testuser", 2026, 10));
    }
}
