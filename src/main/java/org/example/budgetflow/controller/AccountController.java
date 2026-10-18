package org.example.budgetflow.controller;

import org.springframework.security.core.Authentication;
import org.example.budgetflow.entity.Account;
import org.example.budgetflow.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping
    public Account register(Authentication authentication, @RequestBody Account account){
        String login = authentication.getName();
        return this.accountService.registerAccount(account, login);
    }

    @GetMapping("/{id}")
    public Account getById(Authentication authentication, @PathVariable Long id){
        String login = authentication.getName();
        return accountService.getById(id, login);
    }

    @GetMapping
    public List<Account> getAllUserAccounts(Authentication authentication){
        String login = authentication.getName();
        return accountService.getAllAccounts(login);
    }

    @DeleteMapping("/{id}")
    public void removeAccount(Authentication authentication, @PathVariable Long id){
        String login = authentication.getName();
        accountService.removeAccount(id, login);
    }

    @PutMapping("/{id}")
    public void updateAccount(Authentication authentication, @PathVariable Long id, @RequestBody Account account){
        String login = authentication.getName();
        Account existingAccount = accountService.getById(id, login);
        existingAccount.setName(account.getName());
        existingAccount.setBalance(account.getBalance());
        existingAccount.setCurrency(account.getCurrency());
        accountService.updateAccount(existingAccount, login);
    }

}
