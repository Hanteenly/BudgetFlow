package org.example.budgetflow.controller;

import jakarta.servlet.ServletConfig;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.exceptions.NoCategoryException;
import org.example.budgetflow.service.CategoryService;
import org.example.budgetflow.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/category")
public class CategoryController {

    private CategoryService categoryService;
    private TransactionService transactionService;

    public CategoryController(CategoryService categoryService, TransactionService transactionService){
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable long id){
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    public void registerCategory(@RequestBody Category category){
        categoryService.createCategory(category);
    }
    @GetMapping
    public List<Category> getAll(){
        return categoryService.getAllCategories();
    }
    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody Category category){
        Category existingCategory = categoryService.getCategoryById(id);
        existingCategory.setName(category.getName());
        categoryService.updateCategory(existingCategory);
    }
    @DeleteMapping("/{id}")
    public void removeCategory(@PathVariable long id){
        List<Transaction> allTransaction = transactionService.getAllTransactions();
        boolean trust = true;
        Category category = categoryService.getCategoryById(id);
        for(Transaction transaction : allTransaction){
            if(transaction.getCategory().getId().equals(category.getId())){
                trust = false;
            }
        }
        if(trust) {
            categoryService.removeCategory(category);
        }else{
            throw new NoCategoryException("Category already used");
        }
    }
}
