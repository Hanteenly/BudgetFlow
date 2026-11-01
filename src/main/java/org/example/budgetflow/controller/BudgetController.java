package org.example.budgetflow.controller;

import org.example.budgetflow.DTO.BudgetSummary;
import org.example.budgetflow.Repository.BudgetRepository;
import org.example.budgetflow.entity.Budget;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.service.BudgetService;
import org.example.budgetflow.service.CategoryService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private BudgetService budgetService;
    private CategoryService categoryService;

    public BudgetController(BudgetService budgetService, CategoryService categoryService){
        this.budgetService = budgetService;
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}/summary")
    public BudgetSummary getBudgetSummary(Authentication authentication, @PathVariable Long id){
        String login = authentication.getName();
        return budgetService.getSummary(login, budgetService.getBudgetById(login, id));
    }
    @GetMapping("/{id}/spent")
    public BigDecimal getSpent(Authentication authentication, @PathVariable Long id) {
        String login = authentication.getName();
        Budget budget = budgetService.getBudgetById(login, id);
        return budgetService.spent(login, budget);
    }

    @PostMapping
    public void registerBudger(Authentication authentication, @RequestBody Budget budget){
        String login = authentication.getName();
        this.budgetService.createBudget(login, budget);
    }
    @GetMapping("/search")
    public Budget getBudget(Authentication authentication, @RequestParam Long category_id, @RequestParam YearMonth date){
        String login = authentication.getName();
        Category category = categoryService.getCategoryById(login, category_id);
        return budgetService.getBudget(login, category, date);
    }

    @GetMapping("/{id}")
    public Budget getBudgetById(Authentication authentication, @PathVariable Long id){
        String login = authentication.getName();
        return budgetService.getBudgetById(login, id);
    }

    @GetMapping
    public List<Budget> getAllBudget(Authentication authentication){
        String login = authentication.getName();
        return budgetService.getAllBudget(login);
    }

    @DeleteMapping("/{id}")
    public void removeBudget(Authentication authentication, @PathVariable Long id){
        String login = authentication.getName();
        budgetService.removeBudget(login, id);
    }

    @PutMapping("/{id}")
    public void updateBudget(Authentication authentication, @PathVariable Long id, @RequestBody Budget budget){
        String login = authentication.getName();
        Budget existingBudget = budgetService.getBudgetById(login, id);
        existingBudget.setCategory(budget.getCategory());
        existingBudget.setLimit(budget.getLimit());
        existingBudget.setPeriod(budget.getPeriod());
        budgetService.updateBudget(login, existingBudget);
    }
}
