package org.example.budgetflow.controller;

import org.example.budgetflow.DTO.AccountReport;
import org.example.budgetflow.DTO.CategoryExpenseReport;
import org.example.budgetflow.DTO.MonthlyReport;
import org.example.budgetflow.service.ReportService;
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
    public MonthlyReport getMonthlyReport(@RequestParam int year, @RequestParam int month){
        return reportService.getMonthlyReport(year, month);
    }

    @GetMapping("/expenses-by-category")
    public List<CategoryExpenseReport> getCategoryExpenseReport(@RequestParam int year, @RequestParam int month){
        return reportService.getExpensesByCategory(year, month);
    }

    @GetMapping("/accounts")
    public List<AccountReport> getAccountsReport(){
        return reportService.getAccountsReport();
    }

}
