package org.example.budgetflow.Repository;

import org.example.budgetflow.DTO.AccountReport;
import org.example.budgetflow.entity.Account;
import org.example.budgetflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>{

    @Query("SELECT a.name, a.balance " +
            "FROM Account a")
    public List<AccountReport> getAccountReports();
    public List<Account> getAccountsByUser(User user);
    public Optional<Account> getAccountByIdAndUser(Long id, User user);
}
