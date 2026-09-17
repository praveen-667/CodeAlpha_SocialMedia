package com.codealpha.socialmedia.service;

import com.codealpha.socialmedia.model.Post;
import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(String content, User author) {
        Post post = new Post(content, author);
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}