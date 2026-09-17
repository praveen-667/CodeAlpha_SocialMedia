package com.codealpha.socialmedia.controller;

import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String users(
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        List<User> users =
                userRepository.findAll();

        model.addAttribute(
                "loggedInUser",
                loggedInUser
        );

        model.addAttribute(
                "users",
                users
        );

        return "users";
    }
}