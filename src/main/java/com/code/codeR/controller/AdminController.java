package com.code.codeR.controller;

import com.code.codeR.model.User;
import com.code.codeR.repository.CodingProblemRepository;
import com.code.codeR.repository.QuizQuestionRepository;
import com.code.codeR.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {

    private final UserRepository userRepository;
    private final CodingProblemRepository codingProblemRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        long userCount = userRepository.count();
        long quizCount = quizQuestionRepository.count();
        long problemCount = codingProblemRepository.count();
        
        stats.put("totalUsers", userCount);
        stats.put("activeQuizzes", quizCount); 
        stats.put("totalProblems", problemCount);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
