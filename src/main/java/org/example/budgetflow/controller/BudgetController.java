package org.example.budgetflow.controller;

import org.example.budgetflow.DTO.BudgetSummary;
import org.example.budgetflow.Repository.BudgetRepository;
import org.example.budgetflow.entity.Budget;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.service.BudgetService;
import org.example.budgetflow.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/budget")
public class BudgetController {

    private BudgetService budgetService;
    private CategoryService categoryService;

    public BudgetController(BudgetService budgetService, CategoryService categoryService){
        this.budgetService = budgetService;
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}/summary")
    public BudgetSummary getBudgetSummary(@PathVariable Long id){
        return budgetService.getSummary(budgetService.getBudgetById(id));
    }
    @GetMapping("/{id}/spent")
    public BigDecimal getSpent(@PathVariable Long id) {
        Budget budget = budgetService.getBudgetById(id);
        return budgetService.spent(budget);
    }

    @PutMapping
    public void registerBudger(@RequestBody Budget budget){
        this.budgetService.createBudget(budget);
    }
    @GetMapping("/search")
    public Budget getBudget(@RequestParam Long category_id, @RequestParam YearMonth date){
        Category category = categoryService.getCategoryById(category_id);
        return budgetService.getBudget(category, date);
    }

    @GetMapping("/{id}")
    public Budget getBudgetById(@PathVariable Long id){
        return budgetService.getBudgetById(id);
    }

    @GetMapping
    public List<Budget> getAllBudget(){
        return budgetService.getAllBudget();
    }

    @DeleteMapping("/{id}")
    public void removeBudget(@PathVariable Long id){
        budgetService.removeBudget(id);
    }

    @PutMapping("/{id}")
    public void updateBudget(@PathVariable Long id, @RequestBody Budget budget){
        Budget existingBudget = budgetService.getBudgetById(id);
        existingBudget.setCategory(budget.getCategory());
        existingBudget.setLimit(budget.getLimit());
        existingBudget.setPeriod(budget.getPeriod());
        budgetService.updateBudget(existingBudget);
    }
}
