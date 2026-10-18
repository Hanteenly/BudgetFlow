package org.example.budgetflow.DTO;

import org.example.budgetflow.entity.Category;

import java.math.BigDecimal;

public class CategoryExpenseReport {

    private Category category;
    private BigDecimal amount;

    public CategoryExpenseReport(Category category, BigDecimal amount){
        this.category = category;
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
