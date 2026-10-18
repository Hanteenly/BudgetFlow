package org.example.budgetflow.Repository;

import org.example.budgetflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByName(String name);
        Boolean existsByName(String name);
}
