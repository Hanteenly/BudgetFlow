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
    public void removeAccount(long id){
        if(accountRepository.findById(id).isPresent())accountRepository.deleteById(id);
        else throw new AccountNotFoundException("Account not found with id: " + id);
    }
    public Account getByid(long id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }
    public void updateAccount(Account account){
        accountRepository.save(account);
    }
}
