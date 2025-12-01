package com.team4.wildlifetracker.service;


import com.team4.wildlifeTracker.model.User;
import com.team4.wildlifeTracker.repository.UserRepository;
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

    public User register(String username, String password) {
        username = username.toLowerCase();
        if (repo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Invalid login");
        }

        User u = new User(username, enc.encode(password));
        return repo.save(u);
    }

    public Optional<User> login(String username, String password) {
        username = username.toLowerCase();
        Optional<User> u = repo.findByUsername(username);
        if (u.isEmpty()) return Optional.empty();

        if (!u.get().getPassword().equals(enc.encode(password))) {
            return Optional.empty();
        }
        return u;
    }
}