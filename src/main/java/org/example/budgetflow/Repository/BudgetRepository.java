package org.example.budgetflow.Repository;

import org.example.budgetflow.DTO.BudgetSummary;
import org.example.budgetflow.entity.Budget;
import org.example.budgetflow.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Period;
import java.time.YearMonth;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    public Optional<Budget> findByCategoryAndPeriod(Category category, YearMonth period);

}