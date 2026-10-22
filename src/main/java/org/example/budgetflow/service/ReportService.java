package org.example.budgetflow.service;

import org.example.budgetflow.DTO.AccountReport;
import org.example.budgetflow.DTO.CategoryExpenseReport;
import org.example.budgetflow.DTO.MonthlyReport;
import org.example.budgetflow.Repository.AccountRepository;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.Repository.UserRepository;
import org.example.budgetflow.entity.Account;
import org.example.budgetflow.entity.User;
import org.example.budgetflow.exceptions.UserNoFindException;
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
    private UserRepository userRepositor;

    public ReportService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository){
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepositor = userRepository;
    }

    public List<AccountReport> getAccountsReport(String login){
        User user = userRepositor.findByName(login).orElseThrow(() -> new UserNoFindException("Not foud user"));
        List<AccountReport> result = new ArrayList<>();
        for(Account a : accountRepository.getAccountsByUser(user)){
            AccountReport ar = new AccountReport(a.getName(), a.getBalance());
            result.add(ar);
        }
        return result;
    }

    public List<CategoryExpenseReport> getExpensesByCategory(String login, int year, int month){
        User user = userRepositor.findByName(login).orElseThrow(() -> new UserNoFindException("Not foud user"));
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atDay(1).atStartOfDay().plusMonths(1);
        return transactionRepository.getCategoryExpenseReport(start, end, user);
    }
    public MonthlyReport getMonthlyReport(String login, int year, int month){
        User user = userRepositor.findByName(login).orElseThrow(() -> new UserNoFindException("Not foud user"));
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atDay(1).atStartOfDay().plusMonths(1);
        BigDecimal income = transactionRepository.sumIncome(start, end, user);
        BigDecimal expense = transactionRepository.sumExpense(start, end, user);
        BigDecimal net = income.subtract(expense);
        MonthlyReport monthlyReport = new MonthlyReport(income, expense, net);
        return monthlyReport;
    }
}
