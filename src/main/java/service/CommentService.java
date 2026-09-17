package com.codealpha.socialmedia.service;

import com.codealpha.socialmedia.model.Comment;
import com.codealpha.socialmedia.model.Post;
import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.repository.CommentRepository;
import com.codealpha.socialmedia.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository) {

        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public Comment addComment(
            String content,
            User author,
            Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new RuntimeException("Post not found"));

        Comment comment = new Comment(
                content,
                author,
                post
        );

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsForPost(Long postId) {
        return commentRepository
                .findByPostIdOrderByCreatedAtAsc(postId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}