package com.code.codeR.repository;

import com.code.codeR.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByCategoryIdAndDifficulty(Long categoryId, String difficulty);
    List<QuizQuestion> findByCategoryId(Long categoryId);
    List<QuizQuestion> findByTopicId(Long topicId);
}
