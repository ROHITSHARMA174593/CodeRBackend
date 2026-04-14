package com.code.codeR.repository;

import com.code.codeR.model.CodingProblem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CodingProblemRepository extends JpaRepository<CodingProblem, Long> {
    List<CodingProblem> findByCategoryId(Long categoryId);
    List<CodingProblem> findByTopicId(Long topicId);

    @EntityGraph(attributePaths = {"testCases"})
    @Query("SELECT p FROM CodingProblem p WHERE p.id = :id")
    Optional<CodingProblem> findByIdWithTestCases(@Param("id") Long id);
}
