package com.codealpha.socialmedia.service;

import com.codealpha.socialmedia.model.Follow;
import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.repository.FollowRepository;
import com.codealpha.socialmedia.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(
            FollowRepository followRepository,
            UserRepository userRepository) {

        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public void toggleFollow(
            Long followerId,
            Long followingId) {

        if (followerId.equals(followingId)) {
            throw new RuntimeException(
                    "You cannot follow yourself."
            );
        }

        var existingFollow =
                followRepository.findByFollowerIdAndFollowingId(
                        followerId,
                        followingId
                );

        if (existingFollow.isPresent()) {

            followRepository.delete(
                    existingFollow.get()
            );

        } else {

            User follower = userRepository.findById(followerId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Follower not found"
                            ));

            User following = userRepository.findById(followingId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "User to follow not found"
                            ));

            Follow follow = new Follow(
                    follower,
                    following
            );

            followRepository.save(follow);
        }
    }

    public boolean isFollowing(
            Long followerId,
            Long followingId) {

        return followRepository
                .findByFollowerIdAndFollowingId(
                        followerId,
                        followingId
                )
                .isPresent();
    }

    public long getFollowerCount(Long userId) {

        return followRepository
                .countByFollowingId(userId);
    }

    public long getFollowingCount(Long userId) {

        return followRepository
                .countByFollowerId(userId);
    }
}