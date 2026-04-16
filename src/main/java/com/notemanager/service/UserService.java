package com.notemanager.service;

import com.notemanager.model.User;
import com.notemanager.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        String username = clean(user.getUsername());
        String email = clean(user.getEmail());
        String password = clean(user.getPassword());

        if (isBlank(username) || isBlank(email) || isBlank(password)) {
            throw new IllegalArgumentException("Username, email, and password are required");
        }

        email = email.toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        return userRepository.save(newUser);
    }

    public User loginUser(String username, String password) {
        String cleanUsername = clean(username);
        String cleanPassword = clean(password);

        if (isBlank(cleanUsername) || isBlank(cleanPassword)) {
            throw new IllegalArgumentException("Username and password are required");
        }

        User user = userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (user.getPassword().equals(cleanPassword)) {
            return user;
        }

        if (isLegacyHashedPassword(user.getPassword()) && passwordEncoder.matches(cleanPassword, user.getPassword())) {
            // One-time migration for users created by the older BCrypt-based version.
            user.setPassword(cleanPassword);
            return userRepository.save(user);
        }

        throw new IllegalArgumentException("Invalid username or password");
    }

    public User getUserByUsername(String username) {
        String cleanUsername = clean(username);

        if (isBlank(cleanUsername)) {
            throw new IllegalArgumentException("Username is required");
        }

        return userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isLegacyHashedPassword(String password) {
        return password != null
                && (password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$"));
    }
}
