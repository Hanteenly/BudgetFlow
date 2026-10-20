package org.example.budgetflow.controller;

import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.service.TransactionService;
import org.springframework.security.core.Authentication;
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
    public void transfer(Authentication authentication,
                         @RequestParam long sourceAccountId,
                         @RequestParam long destinationAccountId,
                         @RequestParam BigDecimal amount){
        transactionService.transfer(authentication.getName(), sourceAccountId, destinationAccountId, amount);
    }

    @GetMapping("/accounts/{id}")
    public List<Transaction> getAllTransactionByAccountId(Authentication authentication, @PathVariable long id){
        return transactionService.getAllTransactionByAccountId(authentication.getName(), id);
    }

    @PostMapping
    public void register(Authentication authentication, @RequestBody Transaction transaction){
        transactionService.createTransaction(authentication.getName(), transaction);
    }

    @GetMapping
    public List<Transaction> getAllTransaction(Authentication authentication){
         return transactionService.getAllTransactions(authentication.getName());
    }

    @GetMapping("/{id}")
    public Transaction getById(Authentication authentication, @PathVariable long id){
        return transactionService.getById(authentication.getName(), id);
    }

    @DeleteMapping("/{id}")
    public void removeTransaction(Authentication authentication, @PathVariable long id){
        transactionService.removeTransaction(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public void update(Authentication authentication, @PathVariable long id, @RequestBody Transaction transaction){
        Transaction transaction2 = transactionService.getById(authentication.getName(), id);
        transaction2.setAmount(transaction.getAmount());
        transaction2.setType(transaction.getType());
        transaction2.setAccount(transaction.getAccount());
        transaction2.setCategory(transaction.getCategory());
        transaction2.setCreatedAt(transaction.getCreatedAt());
        transactionService.updateTransaction(authentication.getName(), transaction2);
    }
}
