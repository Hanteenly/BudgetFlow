package org.example.budgetflow.DTO;

import org.example.budgetflow.entity.Account;

import java.math.BigDecimal;
import java.text.Bidi;

public class AccountReport {

    private String account;
    private BigDecimal balance;

    public AccountReport(String account, BigDecimal balance){
        this.account = account;
        this.balance = balance;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
