package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.UserResponse;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

    public AuthService(UserRepository repo) {
        this.repo = repo;
    }

    // REGISTER ---------------------------------------------
    public UserResponse registerUser(String username, String password) {
        username = username.toLowerCase();

        // Since repository does NOT have existsByUsername()
        if (repo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        String hashedPassword = enc.encode(password);

        User newUser = new User(username, hashedPassword);

        User saved = repo.save(newUser);

        return UserResponse.fromEntity(saved);
    }

    // LOGIN ------------------------------------------------
    public Optional<UserResponse> login(String username, String password) {
        username = username.toLowerCase();

        Optional<User> userOpt = repo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        if (!enc.matches(password, user.getPassword())) {
            return Optional.empty();
        }

        return Optional.of(UserResponse.fromEntity(user));
    }
}