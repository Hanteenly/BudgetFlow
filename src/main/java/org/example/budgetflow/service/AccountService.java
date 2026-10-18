package org.example.budgetflow.service;

import jakarta.validation.Valid;
import org.example.budgetflow.Repository.AccountRepository;
import org.example.budgetflow.Repository.UserRepository;
import org.example.budgetflow.entity.Account;
import org.example.budgetflow.entity.User;
import org.example.budgetflow.exceptions.AccountNotFoundException;
import org.example.budgetflow.exceptions.UserNoFindException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, UserRepository userRepository){
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account registerAccount(@Valid Account account, String login){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User is not exist with this login"));
        account.setUser(user);
        return accountRepository.save(account);
    }
    public List<Account> getAllAccounts(String login){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("User is not exist with this login"));
        return accountRepository.getAccountsByUser(user);
    }
    public void removeAccount(long id, String login){
        User user = userRepository.findByName(login)
                .orElseThrow(() -> new UserNoFindException("User is not exist with this login"));
        Account account = accountRepository.getAccountByIdAndUser(id, user)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        accountRepository.delete(account);
    }
    public Account getById(long id, String login){
        User user = userRepository.findByName(login)
                .orElseThrow(() -> new UserNoFindException("User is not exist with this login"));
        return accountRepository.getAccountByIdAndUser(id, user)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }
    public void updateAccount(Account account, String login){
        User user = userRepository.findByName(login)
                .orElseThrow(() -> new UserNoFindException("User is not exist with this login"));
        accountRepository.getAccountByIdAndUser(account.getId(), user)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + account.getId()));
        accountRepository.save(account);
    }
}
