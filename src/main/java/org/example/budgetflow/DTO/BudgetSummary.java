package org.example.budgetflow.DTO;

import org.example.budgetflow.entity.Category;
import org.example.budgetflow.service.BudgetService;

import java.math.BigDecimal;

public class BudgetSummary {
    private Category category;
    private BigDecimal limit;
    private BigDecimal spent;
    private BigDecimal remaining;
    private boolean overLimit;

    public BudgetSummary(Category category,
                         BigDecimal limit,
                         BigDecimal spent,
                         BigDecimal remaining,
                         boolean overLimit){
        this.category = category;
        this.limit = limit;
        this.spent = spent;
        this.remaining = remaining;
        this.overLimit = overLimit;
    }

    public void setCategory(Category category){
        this.category = category;
    }
    public Category getCategory() {
        return category;
    }

    public BigDecimal getLimit() {
        return limit;
    }
    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public void setRemaining(BigDecimal remaining) {
        this.remaining = remaining;
    }
    public BigDecimal getRemaining() {
        return remaining;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }
    public BigDecimal getSpent() {
        return spent;
    }

    public void setOverLimit(boolean overLimit) {
        this.overLimit = overLimit;
    }
    public boolean getOverLimit(){
        return this.overLimit;
    }
}
