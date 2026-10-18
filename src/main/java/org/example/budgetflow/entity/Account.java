package org.example.budgetflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @PositiveOrZero
    private BigDecimal balance;
    private String currency;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Account(){
    }
    public Account(String name, BigDecimal balance, String currency){
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }
    public void minusBalance(BigDecimal amount){
        this.balance = this.balance.subtract(amount);
    }
    public void addBalance(BigDecimal amount){
        this.balance = this.balance.add(amount);
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public User getUser(){
        return this.user;
    }
    public void setUser(User user){
        this.user = user;
    }
}
