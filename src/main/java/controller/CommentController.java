package com.codealpha.socialmedia.controller;

import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comments/add")
    public String addComment(
            @RequestParam("content") String content,
            @RequestParam("postId") Long postId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (content == null || content.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Comment cannot be empty."
            );

            return "redirect:/";
        }

        commentService.addComment(
                content.trim(),
                loggedInUser,
                postId
        );

        return "redirect:/";
    }

    @PostMapping("/comments/delete")
    public String deleteComment(
            @RequestParam("id") Long commentId,
            HttpSession session) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        commentService.deleteComment(commentId);

        return "redirect:/";
    }
}