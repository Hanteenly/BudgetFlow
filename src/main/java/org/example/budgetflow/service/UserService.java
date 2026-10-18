package org.example.budgetflow.service;

import org.example.budgetflow.Repository.UserRepository;
import org.example.budgetflow.entity.User;
import org.example.budgetflow.exceptions.UserAlreadyExistsException;
import org.example.budgetflow.exceptions.UserNoFindException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User createUser(User user){
        if(userRepository.existsByName(user.getName())){
            throw new UserAlreadyExistsException("User with this name already exist");
        }else{
            String password = passwordEncoder.encode(user.getPassword());
            user.setPassword(password);
            return userRepository.save(user);
        }

    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new UserNoFindException("User is not exist"));
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public void updateUser(User user){
        userRepository.save(user);
    }

}
