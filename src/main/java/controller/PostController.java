package com.codealpha.socialmedia.controller;

import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.service.CommentService;
import com.codealpha.socialmedia.service.LikeService;
import com.codealpha.socialmedia.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostController(
            PostService postService,
            CommentService commentService,
            LikeService likeService) {

        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping("/")
    public String home(
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        model.addAttribute(
                "loggedInUser",
                loggedInUser
        );

        var posts = postService.getAllPosts();

        model.addAttribute(
                "posts",
                posts
        );

        // Comments for each post
        Map<Long, Object> postComments = new HashMap<>();

        // Likes for each post
        Map<Long, Long> postLikes = new HashMap<>();

        // Whether current user liked each post
        Map<Long, Boolean> likedPosts = new HashMap<>();

        for (var post : posts) {

            postComments.put(
                    post.getId(),
                    commentService.getCommentsForPost(
                            post.getId()
                    )
            );

            postLikes.put(
                    post.getId(),
                    likeService.getLikeCount(
                            post.getId()
                    )
            );

            if (loggedInUser != null) {

                likedPosts.put(
                        post.getId(),
                        likeService.isLikedByUser(
                                loggedInUser.getId(),
                                post.getId()
                        )
                );

            } else {

                likedPosts.put(
                        post.getId(),
                        false
                );
            }
        }

        model.addAttribute(
                "postComments",
                postComments
        );

        model.addAttribute(
                "postLikes",
                postLikes
        );

        model.addAttribute(
                "likedPosts",
                likedPosts
        );

        return "home";
    }

    @PostMapping("/posts/create")
    public String createPost(
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (content == null ||
                content.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Post cannot be empty."
            );

            return "redirect:/";
        }

        postService.createPost(
                content.trim(),
                loggedInUser
        );

        return "redirect:/";
    }

    @PostMapping("/posts/delete")
    public String deletePost(
            @RequestParam("id") Long id,
            HttpSession session) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        postService.deletePost(id);

        return "redirect:/";
    }
}