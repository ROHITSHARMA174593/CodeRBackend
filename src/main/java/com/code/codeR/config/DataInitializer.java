package com.code.codeR.config;

import com.code.codeR.model.SkillCategory;
import com.code.codeR.model.User;
import com.code.codeR.repository.SkillCategoryRepository;
import com.code.codeR.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SkillCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Create Categories (Idempotent check)
        createCategoryIfNotFound("HTML", "HyperText Markup Language");
        createCategoryIfNotFound("CSS", "Cascading Style Sheets");
        createCategoryIfNotFound("JavaScript", "Logic for the web");
        createCategoryIfNotFound("Java", "Object-oriented programming");
        createCategoryIfNotFound("DSA", "Data Structures and Algorithms");
        
        
        createDefaultUsers();
        System.out.println("--- CATEGORIES & USERS CHECKED/INITIALIZED ---");
        System.out.println("Total Categories in DB: " + categoryRepository.findAll().size());
    }

    private void createCategoryIfNotFound(String name, String description) {
        if (categoryRepository.findByName(name).isEmpty()) {
            SkillCategory category = new SkillCategory(null, name, description);
            categoryRepository.save(category);
            System.out.println("Created category: " + name);
        }
    }

    private void createDefaultUsers() {
        // 4. Create Default User (Test User)
        if (!userRepository.existsByEmail("test@coder.com")) {
            User user = new User();
            user.setName("Test User");
            user.setEmail("test@coder.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("USER");
            userRepository.save(user);
            System.out.println("--- TEST USER CREATED ---");
        }

        // 5. Create Default Admin
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = new User();
            admin.setName("Admin User(rohitsharma)");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("rohitsharma"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("--- ADMIN USER CREATED (admin@gmail.com) ---");
        }

        if (!userRepository.existsByEmail("rohit@gmail.com")) {
            User admin = new User();
            admin.setName("Rohit Sharma(rohitsharma)");
            admin.setEmail("rohit@gmail.com");
            admin.setPassword(passwordEncoder.encode("rohitsharma"));
            admin.setRole("USER");
            userRepository.save(admin);
            System.out.println("--- Rohit- USER CREATED (admin@gmail.com) ---");
        }
    }


}
