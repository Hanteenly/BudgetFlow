package org.example.budgetflow.service;

import org.example.budgetflow.Repository.AccountRepository;
import org.example.budgetflow.Repository.CategoryRepository;
import org.example.budgetflow.Repository.TransactionRepository;
import org.example.budgetflow.Repository.UserRepository;
import org.example.budgetflow.entity.Account;
import org.example.budgetflow.entity.Category;
import org.example.budgetflow.entity.Transaction;
import org.example.budgetflow.entity.User;
import org.example.budgetflow.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Account sender;
    private Account receiver;
    private Category transferCategory;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "hash");
        sender = new Account("Cash", new BigDecimal("500"), "EUR");
        sender.setId(1L);
        receiver = new Account("Card", new BigDecimal("50"), "EUR");
        receiver.setId(2L);
        transferCategory = new Category("TRANSFER");

        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));
        when(accountRepository.getAccountByIdAndUser(1L, user)).thenReturn(Optional.of(sender));
        when(accountRepository.getAccountByIdAndUser(2L, user)).thenReturn(Optional.of(receiver));
        when(categoryRepository.findByName("TRANSFER")).thenReturn(Optional.of(transferCategory));
    }

    @Test
    void transfer_movesMoneyBetweenOwnedAccounts() {
        transactionService.transfer("testuser", 1L, 2L, new BigDecimal("100"));

        assertEquals(0, new BigDecimal("400").compareTo(sender.getBalance()));
        assertEquals(0, new BigDecimal("150").compareTo(receiver.getBalance()));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transfer_throwsWhenBalanceIsInsufficient() {
        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.transfer("testuser", 1L, 2L, new BigDecimal("600"))
        );

        assertEquals(0, new BigDecimal("500").compareTo(sender.getBalance()));
        assertEquals(0, new BigDecimal("50").compareTo(receiver.getBalance()));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
