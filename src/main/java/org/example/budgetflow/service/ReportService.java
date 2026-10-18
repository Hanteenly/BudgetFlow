package org.example.budgetflow.service;

import org.example.budgetflow.DTO.AccountReport;
import org.example.budgetflow.DTO.CategoryExpenseReport;
import org.example.budgetflow.DTO.MonthlyReport;
import org.example.budgetflow.Repository.AccountRepository;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.entity.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;

    public ReportService(TransactionRepository transactionRepository, AccountRepository accountRepository){
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<AccountReport> getAccountsReport(){
        List<AccountReport> result = new ArrayList<>();
        for(Account a : accountRepository.findAll()){
            AccountReport ar = new AccountReport(a.getName(), a.getBalance());
            result.add(ar);
        }
        return result;
    }

    public List<CategoryExpenseReport> getExpensesByCategory(int year, int month){
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atDay(1).atStartOfDay().plusMonths(1);
        return transactionRepository.getCategoryExpenseReport(start, end);
    }
    public MonthlyReport getMonthlyReport(int year, int month){
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atDay(1).atStartOfDay().plusMonths(1);
        BigDecimal income = transactionRepository.sumIncome(start, end);
        BigDecimal expense = transactionRepository.sumExpense(start, end);
        BigDecimal net = income.subtract(expense);
        MonthlyReport monthlyReport = new MonthlyReport(income, expense, net);
        return monthlyReport;
    }
}
