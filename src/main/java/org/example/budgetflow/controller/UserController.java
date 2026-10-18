package org.example.budgetflow.controller;

import org.example.budgetflow.entity.User;
import org.example.budgetflow.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/User")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public void registerUser(@RequestBody User user){
        userService.createUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody User user){
        User exitingUser = userService.getUserById(id);
        exitingUser.setPassword(user.getPassword());
        exitingUser.setName(user.getName());
        userService.updateUser(exitingUser);
    }
}
