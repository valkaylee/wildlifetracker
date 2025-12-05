package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.ProfileUpdateRequest;
import com.team4.wildlifetracker.dto.UserResponse;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String UPLOAD_DIR = "uploads/profile-pictures/";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public UserResponse registerUser(String username, String password) {
        // Prevent duplicate usernames
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User newUser = new User(username, passwordEncoder.encode(password));
        User saved = userRepository.save(newUser);
        return toUserResponse(saved);
    }

    public Optional<UserResponse> login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return Optional.of(toUserResponse(user.get()));
        }

        return Optional.empty();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<UserResponse> findByIdAsDto(Long id) {
        return userRepository.findById(id).map(this::toUserResponse);
    }
    
    public UserResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getDisplayName() != null && !request.getDisplayName().trim().isEmpty()) {
            user.setDisplayName(request.getDisplayName());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File must be an image");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = "profile_" + userId + "_" + UUID.randomUUID() + extension;

        // Save file
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update user profile picture URL
        String fileUrl = "/uploads/profile-pictures/" + filename;
        user.setProfilePictureUrl(fileUrl);
        userRepository.save(user);

        return fileUrl;
    }
    
    /**
     * Converts User entity to UserResponse DTO.
     * Excludes sensitive information like password.
     */
    public UserResponse toUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getDisplayName(),
            user.getBio(),
            user.getProfilePictureUrl(),
            user.getTotalAnimalsLogged(),
            user.getUniqueSpeciesCount(),
            user.getLastActivityDate()
        );
    }
}
