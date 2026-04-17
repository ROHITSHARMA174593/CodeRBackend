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

    @SuppressWarnings("null")
    public SkillCategory updateCategory(Long id, SkillCategory details) {
        SkillCategory cat = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        cat.setName(details.getName());
        cat.setDescription(details.getDescription());
        return categoryRepository.save(cat);
    }

    @SuppressWarnings("null")
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @SuppressWarnings("null")
    public QuizQuestion updateQuestion(Long id, QuizQuestion details) {
        QuizQuestion q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        q.setQuestion(details.getQuestion());
        q.setOptionA(details.getOptionA());
        q.setOptionB(details.getOptionB());
        q.setOptionC(details.getOptionC());
        q.setOptionD(details.getOptionD());
        q.setCorrectAnswer(details.getCorrectAnswer());
        q.setDifficulty(details.getDifficulty());
        
        if (details.getCategory() != null) q.setCategory(details.getCategory());
        if (details.getTopic() != null) q.setTopic(details.getTopic());
        
        return questionRepository.save(q);
    }

    @SuppressWarnings("null")
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    public List<QuizQuestion> getQuestionsByTopic(Long topicId) {
        return questionRepository.findByTopicId(topicId);
    }

    public List<QuizQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }
}
