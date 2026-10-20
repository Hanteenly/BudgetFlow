package org.example.budgetflow.Repository;

import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Optional<Category> findByIdAndUser(Long id, User user);
    List<Category> findCategoriesByUser(User user);
}
