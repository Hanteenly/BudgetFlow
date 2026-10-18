package org.example.budgetflow.service;

import org.example.budgetflow.Repository.CategoryRepository;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.exceptions.ConstraintViolationException;
import org.example.budgetflow.exceptions.NoCategoryException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public void createCategory(Category category){
        categoryRepository.save(category);
    }

    public Category getCategoryById(long id){
        return categoryRepository.findById(id).orElseThrow(() -> new NoCategoryException("category was not foud"));
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public void removeCategory(Category category){
        categoryRepository.delete(category);
    }

    public void updateCategory(Category category){
        categoryRepository.save(category);
    }
}
