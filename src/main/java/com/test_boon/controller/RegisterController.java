package com.test_boon.controller;

import com.test_boon.domain.UserEntity;
import com.test_boon.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Controller
public class RegisterController {

    public static final String CONTROLLER_NAME = "register";

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/" + CONTROLLER_NAME)
    public String home() {
        return CONTROLLER_NAME;
    }

    @PostMapping("/" + CONTROLLER_NAME)
    public String register(
            @NotBlank
            @RequestParam String login,
            @NotBlank
            @RequestParam String password,
            @NotBlank
            @RequestParam String confirmedPassword,
            Map<String, Object> model
    ) {
        if (userRepository.findByLogin(login).isPresent()) {
            model.put("errorMessage", "Такой логин уже занят.");
            return CONTROLLER_NAME;
        }
        if (!password.equals(confirmedPassword)) {
            model.put("errorMessage", "Пароли не совпадают.");
            return CONTROLLER_NAME;
        }

        userRepository.save(new UserEntity(login, password));

        return "redirect:";
    }

}