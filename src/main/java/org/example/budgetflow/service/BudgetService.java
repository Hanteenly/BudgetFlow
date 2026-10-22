package org.example.budgetflow.service;

import org.example.budgetflow.DTO.BudgetSummary;
import org.example.budgetflow.Repository.BudgetRepository;
import org.example.budgetflow.Repository.CategoryRepository;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.Repository.UserRepository;
import org.example.budgetflow.entity.Budget;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.entity.User;
import org.example.budgetflow.exceptions.BudgetException;
import org.example.budgetflow.exceptions.NoCategoryException;
import org.example.budgetflow.exceptions.UserNoFindException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class BudgetService {

    private BudgetRepository budgetRepository;
    private TransactionRepository transactionRepositor;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;

    public BudgetService(BudgetRepository budgetRepository1, TransactionRepository transactionRepository, UserRepository userRepository, CategoryRepository categoryRepository){
        this.budgetRepository = budgetRepository1;
        this.transactionRepositor = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public void createBudget(String login, Budget budget){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User not found"));
        Category category = categoryRepository.findByIdAndUser(budget.getCategory().getId(), user)
                .orElseThrow(() -> new NoCategoryException("category was not foud"));
        budget.setCategory(category);
        YearMonth yearMonth = budget.getPeriod();
        if(budgetRepository.findByCategoryAndPeriod(category, yearMonth).isEmpty()){
            budgetRepository.save(budget);
        }else{
            throw new BudgetException("This budget already exist");
        }
    }

    public BudgetSummary getSummary(String login, Budget budget){
        BigDecimal spent = spent(login, budget);
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
    public BigDecimal spent(String login, Budget budget){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("No foud user"));
        Category category = budget.getCategory();
        LocalDateTime start = budget.getPeriod().atDay(1).atStartOfDay();
        LocalDateTime end = budget.getPeriod().plusMonths(1).atDay(1).atStartOfDay();
        return transactionRepositor.amountFromMonth(category, start, end, user);
    }

    public Budget getBudgetById(String login, Long id){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User not found"));
        return budgetRepository.findByIdAndCategory_User(id, user).orElseThrow(() -> new BudgetException("not found Budget: " + id));
    }
    public Budget getBudget(String login, Category category, YearMonth date){
        userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User not found"));
        return budgetRepository.findByCategoryAndPeriod(category, date)
                .orElseThrow(() -> new BudgetException("Not found budget"));
    }

    public List<Budget> getAllBudget(String login){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User not found"));
        return budgetRepository.findByCategoryUser(user);
    }

    public void updateBudget(String login, Budget budget){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User not found"));
        budgetRepository.findByIdAndCategory_User(budget.getId(), user)
                .orElseThrow(() -> new BudgetException("not found Budget: " + budget.getId()));
        if (budget.getCategory() != null && budget.getCategory().getId() != null) {
            Category category = categoryRepository.findByIdAndUser(budget.getCategory().getId(), user)
                    .orElseThrow(() -> new NoCategoryException("category was not foud"));
            budget.setCategory(category);
        }
        budgetRepository.save(budget);
    }
    public void removeBudget(String login, Long id){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User not found"));

        if(budgetRepository.findByIdAndCategory_User(id, user).isPresent()) budgetRepository.deleteById(id);
        else throw new BudgetException("not found Budget: " + id);
    }
}
