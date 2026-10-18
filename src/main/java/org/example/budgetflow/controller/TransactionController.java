package org.example.budgetflow.controller;

import org.example.budgetflow.entity.Account;
import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public void transfer(@RequestParam long sourceAccountId, @RequestParam long destinationAccountId, @RequestParam BigDecimal amount){
        transactionService.transfer(sourceAccountId, destinationAccountId, amount);
    }

    @GetMapping("/accounts/{id}")
    public List<Transaction> getAllTransactionByAccountId(@PathVariable long id){
        return transactionService.getAllTransactionByAccountId(id);
    }
    @PostMapping()
    public void register(@RequestBody Transaction transaction){
        transactionService.createTransaction(transaction);
    }
    @GetMapping
    public List<Transaction> getAllTransaction(){
         return transactionService.getAllTransactions();
    }
    @GetMapping("/{id}")
    public Transaction getById(@PathVariable long id){
        return transactionService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void removeTransaction(@PathVariable long id){
        transactionService.removeTransaction(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @RequestBody Transaction transaction){
        Transaction transaction2 = transactionService.getById(id);
        transaction2.setAmount(transaction.getAmount());
        transaction2.setType(transaction.getType());
        transaction2.setAccount(transaction.getAccount());
        transaction2.setCategory(transaction.getCategory());
        transaction2.setCreatedAt(transaction.getCreatedAt());
        transactionService.updateTransaction(transaction2);
    }
}
