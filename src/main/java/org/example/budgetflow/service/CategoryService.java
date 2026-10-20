package org.example.budgetflow.service;

import org.example.budgetflow.Repository.CategoryRepository;
import org.example.budgetflow.Repository.UserRepository;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.User;
import org.example.budgetflow.exceptions.ConstraintViolationException;
import org.example.budgetflow.exceptions.NoCategoryException;
import org.example.budgetflow.exceptions.UserNoFindException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository){
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public void createCategory(String login, Category category){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User is not exist"));
        category.setUser(user);
        categoryRepository.save(category);

    }

    public Category getCategoryById(String login, Long id){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User is not exist"));
        return categoryRepository.findByIdAndUser(id, user).orElseThrow(() -> new NoCategoryException("category was not foud"));
    }

    public List<Category> getAllCategories(String login){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User is not exist"));
        return categoryRepository.findCategoriesByUser(user);
    }

    public void removeCategory(String login, Category category){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User is not exist"));
        Category exitingCategory = categoryRepository.findByIdAndUser(category.getId(), user).orElseThrow(() -> new NoCategoryException("category was not foud"));
        categoryRepository.delete(exitingCategory);
    }

    public void updateCategory(String login, Category category){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User is not exist"));
        Category exitingCategory = categoryRepository.findByIdAndUser(category.getId(), user).orElseThrow(() -> new NoCategoryException("category was not foud"));
        exitingCategory.setName(category.getName());
        categoryRepository.save(exitingCategory);
    }
}
