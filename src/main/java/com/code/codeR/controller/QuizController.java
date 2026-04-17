package com.code.codeR.controller;


import com.code.codeR.model.QuizQuestion;
import com.code.codeR.model.SkillCategory;
import com.code.codeR.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/categories")
    public ResponseEntity<List<SkillCategory>> getAllCategories() {
        return ResponseEntity.ok(quizService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<SkillCategory> createCategory(@RequestBody SkillCategory category) {
        return ResponseEntity.ok(quizService.createCategory(category));
    }

    @GetMapping("/questions/{categoryId}")
    public ResponseEntity<List<QuizQuestion>> getQuestionsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(quizService.getQuestionsByCategory(categoryId));
    }
    
    @GetMapping("/questions/{categoryId}/{difficulty}")
    public ResponseEntity<List<QuizQuestion>> getQuestionsByDifficulty(
            @PathVariable Long categoryId,
            @PathVariable String difficulty) {
        return ResponseEntity.ok(quizService.getQuestionsByCategoryAndDifficulty(categoryId, difficulty));
    }

    @PostMapping("/questions")
    public ResponseEntity<QuizQuestion> createQuestion(@RequestBody QuizQuestion question) {
        return ResponseEntity.ok(quizService.createQuestion(question));
    }

    @GetMapping("/questions/topic/{topicId}")
    public ResponseEntity<List<QuizQuestion>> getQuestionsByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(quizService.getQuestionsByTopic(topicId));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<QuizQuestion> updateQuestion(@PathVariable Long id, @RequestBody QuizQuestion question) {
        return ResponseEntity.ok(quizService.updateQuestion(id, question));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        quizService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<SkillCategory> updateCategory(@PathVariable Long id, @RequestBody SkillCategory category) {
        return ResponseEntity.ok(quizService.updateCategory(id, category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        quizService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
