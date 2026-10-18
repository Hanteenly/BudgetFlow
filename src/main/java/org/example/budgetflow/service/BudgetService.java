package org.example.budgetflow.service;

import org.example.budgetflow.DTO.BudgetSummary;
import org.example.budgetflow.Repository.BudgetRepository;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.entity.Budget;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.exceptions.BudgetException;
import org.example.budgetflow.exceptions.NoCategoryException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class BudgetService {

    private BudgetRepository budgetRepository;
    private TransactionRepository transactionRepositor;

    public BudgetService(BudgetRepository budgetRepository1, TransactionRepository transactionRepository){
        this.budgetRepository = budgetRepository1;
        this.transactionRepositor = transactionRepository;
    }

    public void createBudget(Budget budget){
        Category category = budget.getCategory();
        YearMonth yearMonth = budget.getPeriod();
        if(budgetRepository.findByCategoryAndPeriod(category, yearMonth).isEmpty()){
            budgetRepository.save(budget);
        }else{
            throw new BudgetException("This budget already exist");
        }
    }

    public BudgetSummary getSummary(Budget budget){
        BigDecimal spent = spent(budget);
        boolean overLimit = spent.compareTo(budget.getLimit()) > 0;
        BigDecimal remaining = budget.getLimit().subtract(spent);
        BudgetSummary budgetSummary = new BudgetSummary(
                budget.getCategory(),
                budget.getLimit(),
                spent,
                remaining,
                overLimit
                );
        return budgetSummary;
    }
    public BigDecimal spent(Budget budget){
        Category category = budget.getCategory();
        LocalDateTime start = budget.getPeriod().atDay(1).atStartOfDay();
        LocalDateTime end = budget.getPeriod().plusMonths(1).atDay(1).atStartOfDay();
        return transactionRepositor.amountFromMonth(category, start, end);
    }

    public Budget getBudgetById(Long id){
        return budgetRepository.findById(id).orElseThrow(() -> new BudgetException("not found Budget: " + id));
    }
    public Budget getBudget(Category category, YearMonth date){
        return budgetRepository.findByCategoryAndPeriod(category, date).orElseThrow(() -> new BudgetException("Not found budget"));
    }

    public List<Budget> getAllBudget(){
        return budgetRepository.findAll();
    }

    public void updateBudget(Budget budget){
        budgetRepository.save(budget);
    }
    public void removeBudget(Long id){
        if(budgetRepository.findById(id).isPresent()) budgetRepository.deleteById(id);
        else throw new BudgetException("not found Budget: " + id);
    }
}
