package org.example.budgetflow.controller;

import jakarta.servlet.ServletConfig;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.exceptions.NoCategoryException;
import org.example.budgetflow.service.CategoryService;
import org.example.budgetflow.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private CategoryService categoryService;
    private TransactionService transactionService;

    public CategoryController(CategoryService categoryService, TransactionService transactionService){
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public Category getById(Authentication authentication, @PathVariable long id){
        String login = authentication.getName();
        return categoryService.getCategoryById(login, id);
    }

    @PostMapping
    public void registerCategory(Authentication authentication, @RequestBody Category category){
        String login = authentication.getName();
        categoryService.createCategory(login, category);
    }

    @GetMapping
    public List<Category> getAll(Authentication authentication){
        return categoryService.getAllCategories(authentication.getName());
    }

    @PutMapping("/{id}")
    public void update(Authentication authentication, @PathVariable long id, @RequestBody Category category){
        String login = authentication.getName();
        Category existingCategory = categoryService.getCategoryById(login, id);
        existingCategory.setName(category.getName());
        categoryService.updateCategory(login, existingCategory);
    }
    @DeleteMapping("/{id}")
    public void removeCategory(Authentication authentication, @PathVariable long id){
        String login = authentication.getName();
        List<Transaction> allTransaction = transactionService.getAllTransactions(login);
        boolean trust = true;
        Category category = categoryService.getCategoryById(login, id);
        for(Transaction transaction : allTransaction){
            if(transaction.getCategory().getId().equals(category.getId())){
                trust = false;
            }
        }
        if(trust) {
            categoryService.removeCategory(login, category);
        }else{
            throw new NoCategoryException("Category already used");
        }
    }
}
