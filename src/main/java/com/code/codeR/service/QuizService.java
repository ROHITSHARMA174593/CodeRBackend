package com.code.codeR.service;

import com.code.codeR.model.QuizQuestion;
import com.code.codeR.model.SkillCategory;
import com.code.codeR.repository.QuizQuestionRepository;
import com.code.codeR.repository.SkillCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final SkillCategoryRepository categoryRepository;
    private final QuizQuestionRepository questionRepository;

    public List<SkillCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<QuizQuestion> getQuestionsByCategory(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }
    
    public List<QuizQuestion> getQuestionsByCategoryAndDifficulty(Long categoryId, String difficulty) {
        return questionRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
    }

    @SuppressWarnings("null")
    public SkillCategory createCategory(SkillCategory category) {
        return categoryRepository.save(category);
    }

    @SuppressWarnings("null")
    public QuizQuestion createQuestion(QuizQuestion question) {
        return questionRepository.save(question);
    }

    public List<QuizQuestion> getQuestionsByTopic(Long topicId) {
        return questionRepository.findByTopicId(topicId);
    }
}
