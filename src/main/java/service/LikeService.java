package com.codealpha.socialmedia.service;

import com.codealpha.socialmedia.model.Like;
import com.codealpha.socialmedia.model.Post;
import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.repository.LikeRepository;
import com.codealpha.socialmedia.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeService(
            LikeRepository likeRepository,
            PostRepository postRepository) {

        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    public void toggleLike(Long userId, Long postId) {

        var existingLike =
                likeRepository.findByUserIdAndPostId(
                        userId,
                        postId
                );

        if (existingLike.isPresent()) {

            likeRepository.delete(existingLike.get());

        } else {

            User user = new User();
            user.setId(userId);

            Post post = postRepository.findById(postId)
                    .orElseThrow(() ->
                            new RuntimeException("Post not found"));

            Like like = new Like(user, post);

            likeRepository.save(like);
        }
    }

    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isLikedByUser(
            Long userId,
            Long postId) {

        return likeRepository
                .findByUserIdAndPostId(userId, postId)
                .isPresent();
    }
}