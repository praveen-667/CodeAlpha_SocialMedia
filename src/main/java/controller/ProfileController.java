package com.codealpha.socialmedia.controller;

import com.codealpha.socialmedia.model.User;
import com.codealpha.socialmedia.service.FollowService;
import com.codealpha.socialmedia.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class ProfileController {

    private final UserService userService;
    private final FollowService followService;

    private final Path uploadDirectory =
            Paths.get("uploads/profile-images")
                    .toAbsolutePath()
                    .normalize();

    public ProfileController(
            UserService userService,
            FollowService followService) {

        this.userService = userService;
        this.followService = followService;
    }


    // =========================
    // VIEW PROFILE
    // =========================

    @GetMapping("/profile")
    public String profile(
            @RequestParam("id") Long userId,
            HttpSession session,
            Model model) {

        User profileUser =
                userService.findById(userId);

        if (profileUser == null) {
            return "redirect:/";
        }

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        model.addAttribute(
                "profileUser",
                profileUser
        );

        model.addAttribute(
                "loggedInUser",
                loggedInUser
        );

        model.addAttribute(
                "followerCount",
                followService.getFollowerCount(userId)
        );

        model.addAttribute(
                "followingCount",
                followService.getFollowingCount(userId)
        );

        boolean following = false;

        if (loggedInUser != null &&
                !loggedInUser.getId().equals(userId)) {

            following = followService.isFollowing(
                    loggedInUser.getId(),
                    userId
            );
        }

        model.addAttribute(
                "following",
                following
        );

        return "profile";
    }


    // =========================
    // EDIT PROFILE PAGE
    // =========================

    @GetMapping("/profile/edit")
    public String showEditProfile(
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "user",
                loggedInUser
        );

        return "edit-profile";
    }


    // =========================
    // UPDATE PROFILE
    // =========================

    @PostMapping("/profile/edit")
    public String updateProfile(
            @RequestParam("name") String name,
            @RequestParam("bio") String bio,
            @RequestParam(
                    value = "profileImage",
                    required = false
            )
            MultipartFile profileImage,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (name == null ||
                name.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Name cannot be empty."
            );

            return "redirect:/profile/edit";
        }

        loggedInUser.setName(
                name.trim()
        );

        loggedInUser.setBio(
                bio == null ? "" : bio.trim()
        );


        // =========================
        // PROFILE IMAGE UPLOAD
        // =========================

        if (profileImage != null &&
                !profileImage.isEmpty()) {

            try {

                String originalName =
                        profileImage.getOriginalFilename();

                String extension = "";

                if (originalName != null &&
                        originalName.contains(".")) {

                    extension =
                            originalName.substring(
                                    originalName.lastIndexOf(".")
                            );
                }

                String fileName =
                        UUID.randomUUID()
                                .toString()
                                + extension;

                Files.createDirectories(
                        uploadDirectory
                );

                Path filePath =
                        uploadDirectory
                                .resolve(fileName)
                                .normalize();

                Files.write(
                        filePath,
                        profileImage.getBytes()
                );

                loggedInUser.setProfileImage(
                        fileName
                );

            } catch (IOException e) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "Could not upload profile image."
                );

                return "redirect:/profile/edit";
            }
        }


        User updatedUser =
                userService.updateUser(
                        loggedInUser
                );

        session.setAttribute(
                "loggedInUser",
                updatedUser
        );

        redirectAttributes.addFlashAttribute(
                "success",
                "Profile updated successfully!"
        );

        return "redirect:/profile?id=" +
                updatedUser.getId();
    }


    // =========================
    // DISPLAY PROFILE IMAGE
    // =========================

    @GetMapping("/profile/image/{fileName}")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String fileName) {

        try {

            Path filePath =
                    uploadDirectory
                            .resolve(fileName)
                            .normalize();


            // SECURITY CHECK

            if (!filePath.startsWith(
                    uploadDirectory
            )) {

                return ResponseEntity
                        .badRequest()
                        .build();
            }


            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );


            if (!resource.exists() ||
                    !resource.isReadable()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            String contentType =
                    Files.probeContentType(
                            filePath
                    );


            if (contentType == null) {

                contentType =
                        "application/octet-stream";
            }


            return ResponseEntity
                    .ok()
                    .contentType(
                            MediaType.parseMediaType(
                                    contentType
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    resource.getFilename() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}