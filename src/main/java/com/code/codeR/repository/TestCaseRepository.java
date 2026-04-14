package com.code.codeR.repository;

import com.code.codeR.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByCodingProblemId(Long problemId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TestCase tc WHERE tc.codingProblem.id = :problemId")
    void deleteByCodingProblemId(@Param("problemId") Long problemId);
}
