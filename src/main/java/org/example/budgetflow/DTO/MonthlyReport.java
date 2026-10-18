package org.example.budgetflow.DTO;

import java.math.BigDecimal;

public class MonthlyReport {

    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal net;

    public MonthlyReport(BigDecimal income, BigDecimal expense, BigDecimal net){
        this.income = income;
        this.expense = expense;
        this.net = net;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }

    public BigDecimal getNet() {
        return net;
    }
}

