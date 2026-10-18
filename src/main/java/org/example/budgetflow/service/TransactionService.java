package org.example.budgetflow.service;

import jakarta.transaction.Transactional;
import org.example.budgetflow.Repository.AccountRepository;
import org.example.budgetflow.Repository.CategoryRepository;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.entity.Account;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.Transaction;
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

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, CategoryRepository categoryRepository){
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }


    public List<Transaction> getAllTransactionByAccountId(long id) {
        accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("No Account"));

        return transactionRepository.findByAccountId(id);
    }

    @Transactional
    public void transfer(long sourceAccountId, long destinationAccountId, BigDecimal amount){
        Account senderAccount = accountRepository.findById(sourceAccountId).orElseThrow(() -> new AccountNotFoundException("No account"));
        Account geterAccount = accountRepository.findById(destinationAccountId).orElseThrow(() -> new AccountNotFoundException("No account"));
        Category transfer = categoryRepository.findByName("TRANSFER")
                .orElseThrow(() -> new NoCategoryException("TRANSFER category not found"));

        if(senderAccount.getId().equals(geterAccount.getId())) {
            throw new InvalidTransferException("Same accounts");
        }
        if(amount.compareTo(BigDecimal.ZERO) > 0) {
            if (senderAccount.getBalance().compareTo(amount) >= 0) {
                Transaction transaction1 = new Transaction(
                        amount,
                        "EXPENSE",
                        transfer,
                        LocalDateTime.now(),
                        senderAccount
                );

                Transaction transaction2 = new Transaction(
                        amount,
                        "INCOME",
                        transfer,
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
    public void createTransaction(Transaction transaction){
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new AccountNotFoundException("No account"));
        transaction.setAccount(account);
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

    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }
    public Transaction getById(long id){
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));
    }
    public void removeTransaction(long id){
        if(transactionRepository.findById(id).isPresent()) transactionRepository.deleteById(id);
        else throw new TransactionNotFoundException("Transaction not found with id: " + id);
    }
    public void updateTransaction(Transaction transaction){
        Category category = transaction.getCategory();
        if(categoryRepository.findById(category.getId()).isEmpty()){
            throw new NoCategoryException("No category");
        }
        transactionRepository.save(transaction);
    }
}
