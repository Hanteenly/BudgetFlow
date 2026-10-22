package org.example.budgetflow.controller;

import org.example.budgetflow.DTO.AccountReport;
import org.example.budgetflow.DTO.CategoryExpenseReport;
import org.example.budgetflow.DTO.MonthlyReport;
import org.example.budgetflow.service.ReportService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {
    public ReportService reportService;

    public ReportController(ReportService reportService){
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public MonthlyReport getMonthlyReport(Authentication authentication, @RequestParam int year, @RequestParam int month){
        String login = authentication.getName();
        return reportService.getMonthlyReport(login, year, month);
    }

    @GetMapping("/expenses-by-category")
    public List<CategoryExpenseReport> getCategoryExpenseReport(Authentication authentication, @RequestParam int year, @RequestParam int month){
        String login = authentication.getName();
        return reportService.getExpensesByCategory(login, year, month);
    }

    @GetMapping("/accounts")
    public List<AccountReport> getAccountsReport(Authentication authentication){
        String login = authentication.getName();
        return reportService.getAccountsReport(login);
    }

}
