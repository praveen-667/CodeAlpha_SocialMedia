package com.codealpha.socialmedia.controller;

import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.service.LikeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/likes/toggle")
    public String toggleLike(
            @RequestParam("postId") Long postId,
            HttpSession session) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        likeService.toggleLike(
                loggedInUser.getId(),
                postId
        );

        return "redirect:/";
    }
}