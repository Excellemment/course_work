package com.test_boon.controller;

import com.test_boon.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.Map;

@Scope("session")
@Controller
public class LoginController {

    public static final String CONTROLLER_NAME = "login";

    private UserRepository userRepository;
    private User user;

    @Autowired
    public LoginController(UserRepository userRepository, User user) {
        this.userRepository = userRepository;
        this.user = user;
    }

    @GetMapping
    public String home() {
        if (user.getLogin() != null && !user.getLogin().trim().isEmpty()) {
            return "redirect:/main";
        }
        return CONTROLLER_NAME;
    }

    @PostMapping("login")
    public String login(@RequestParam String login,
                        @RequestParam String password,
                        Map<String, Object> model) {
        return userRepository.findByLogin(login)
                .filter(user -> user.getPassword().equals(password))
                .map(aVoid -> {
                    user.setLogin(login);
                    user.setPassword(password);
                    return ("redirect:" + MainController.CONTROLLER_NAME + "?name=").concat(login);
                })
                .orElseGet(() -> {
                    model.put("errorMessage", "Неправильный логин или пароль");
                    return CONTROLLER_NAME;
                });
    }

}