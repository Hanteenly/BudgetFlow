package org.example.budgetflow.service;

import jakarta.transaction.Transactional;
import org.example.budgetflow.Repository.AccountRepository;
import org.example.budgetflow.Repository.CategoryRepository;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.Repository.UserRepository;
import org.example.budgetflow.entity.Account;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.entity.User;
import org.example.budgetflow.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, CategoryRepository categoryRepository, UserRepository userRepository){
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }


    public List<Transaction> getAllTransactionByAccountId(String login, long id) {
        User user = userRepository.findByName(login)
                .orElseThrow(() -> new UserNoFindException("No user"));
        accountRepository.getAccountByIdAndUser(id, user)
                .orElseThrow(() -> new AccountNotFoundException("No Account"));

        return transactionRepository.findByAccountId(id);
    }

    @Transactional
    public void transfer(String login, long sourceAccountId, long destinationAccountId, BigDecimal amount){
        User user = userRepository.findByName(login)
                .orElseThrow(() -> new UserNoFindException("No user"));
        Account senderAccount = accountRepository.getAccountByIdAndUser(sourceAccountId, user)
                .orElseThrow(() -> new AccountNotFoundException("No account"));
        Account geterAccount = accountRepository.getAccountByIdAndUser(destinationAccountId, user)
                .orElseThrow(() -> new AccountNotFoundException("No account"));

        if(senderAccount.getId().equals(geterAccount.getId())) {
            throw new InvalidTransferException("Same accounts");
        }
        if(amount.compareTo(BigDecimal.ZERO) > 0) {
            if (senderAccount.getBalance().compareTo(amount) >= 0) {
                Transaction transaction1 = new Transaction(
                        amount,
                        "EXPENSE",
                        null,
                        LocalDateTime.now(),
                        senderAccount
                );

                Transaction transaction2 = new Transaction(
                        amount,
                        "INCOME",
                        null,
                        LocalDateTime.now(),
                        geterAccount
                );
                senderAccount.minusBalance(amount);
                geterAccount.addBalance(amount);
                transactionRepository.save(transaction1);
                transactionRepository.save(transaction2);
                accountRepository.save(senderAccount);
                accountRepository.save(geterAccount);
            } else {
                throw new InsufficientBalanceException("no money");
            }
        }else{
            throw new InvalidTransferException("Write bigger amount");
        }
    }

    @Transactional
    public void createTransaction(String login, Transaction transaction){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("Not found user"));
        Account account = accountRepository.getAccountByIdAndUser(transaction.getAccount().getId(), user)
                .orElseThrow(() -> new AccountNotFoundException("No account"));
        transaction.setAccount(account);
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(LocalDateTime.now());
        }
        if(transaction.getType().equals("INCOME")){
            account.addBalance(transaction.getAmount());
        }else if(transaction.getType().equals("EXPENSE")){
            if(transaction.getAmount().compareTo(account.getBalance()) <= 0) {
                account.minusBalance(transaction.getAmount());
            }else{
                throw new InsufficientBalanceException("No enough Money on Balance");
            }
        }else{
            throw new InsufficientBalanceException("Transaction is not available");
        }
        transactionRepository.save(transaction);
        accountRepository.save(account);
    }

    public List<Transaction> getAllTransactions(String login){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("No user"));
        return transactionRepository.getTransactionsByAccountUser(user);
    }
    public Transaction getById(String login, long id){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("No user"));
        return transactionRepository.getTransactionByIdAndAccountUser(id, user)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
    }
    public void removeTransaction(String login, long id){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("No user"));
        Transaction transaction = transactionRepository.getTransactionByIdAndAccountUser(id, user)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
        transactionRepository.delete(transaction);
    }
    public void updateTransaction(String login, Transaction transaction){
        User user = userRepository.findByName(login).orElseThrow(() -> new UserNoFindException("No user"));
        transactionRepository.getTransactionByIdAndAccountUser(transaction.getId(), user)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transaction.getId()));
        if (transaction.getAccount() != null && transaction.getAccount().getId() != null) {
            Account account = accountRepository.getAccountByIdAndUser(transaction.getAccount().getId(), user)
                    .orElseThrow(() -> new AccountNotFoundException("No account"));
            transaction.setAccount(account);
        }
        Category category = transaction.getCategory();
        if(categoryRepository.findById(category.getId()).isEmpty()){
            throw new NoCategoryException("No category");
        }
        transactionRepository.save(transaction);
    }
}
