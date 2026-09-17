package com.codealpha.socialmedia.controller;

import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.service.FollowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow/toggle")
    public String toggleFollow(
            @RequestParam("userId") Long userId,
            HttpSession session) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {

            followService.toggleFollow(
                    loggedInUser.getId(),
                    userId
            );

        } catch (RuntimeException e) {

            // Ignore invalid follow requests
        }

        return "redirect:/";
    }
}