package com.codealpha.socialmedia.controller;

import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // SHOW LOGIN PAGE
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // PROCESS LOGIN
    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(email);

        if (user != null &&
                userService.checkPassword(
                        password,
                        user.getPassword())) {

            session.setAttribute("loggedInUser", user);

            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute(
                "error",
                "Invalid email or password"
        );

        return "redirect:/login";
    }

    // SHOW REGISTER PAGE
    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    // PROCESS REGISTRATION
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute User user,
            RedirectAttributes redirectAttributes) {

        try {

            userService.registerUser(user);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Account created successfully! Please login."
            );

            return "redirect:/login";

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/register";
        }
    }

    // LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}