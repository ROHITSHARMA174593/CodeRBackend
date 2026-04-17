package com.code.codeR.service;

import com.code.codeR.model.CodingProblem;
import com.code.codeR.repository.CodingProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final CodingProblemRepository problemRepository;
    private final TestcaseService testcaseService;
    private final com.code.codeR.repository.UserRepository userRepository;

    public List<com.code.codeR.dto.ProblemDTO> getAllProblemsWithStatus(String email) {
        List<CodingProblem> problems = problemRepository.findAll();
        return mapToDTOs(problems, email);
    }

    public List<com.code.codeR.dto.ProblemDTO> getProblemsByCategoryWithStatus(Long categoryId, String email) {
        List<CodingProblem> problems = problemRepository.findByCategoryId(categoryId);
        return mapToDTOs(problems, email);
    }

    private List<com.code.codeR.dto.ProblemDTO> mapToDTOs(List<CodingProblem> problems, String email) {
        java.util.Set<Long> solvedProblemIds = new java.util.HashSet<>();
        if (email != null) {
            userRepository.findByEmailWithSolvedProblems(email).ifPresent(user -> {
                user.getSolvedProblems().forEach(p -> solvedProblemIds.add(p.getId()));
            });
        }
        
        return problems.stream()
                .map(p -> com.code.codeR.dto.ProblemDTO.fromEntity(p, solvedProblemIds.contains(p.getId())))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<CodingProblem> getProblemsByCategory(Long categoryId) {
        return problemRepository.findByCategoryId(categoryId);
    }

    public List<CodingProblem> getAllProblems() {
        return problemRepository.findAll();
    }

    @SuppressWarnings("null")
    public CodingProblem getProblemById(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
    }

    @Transactional
    public CodingProblem createProblem(CodingProblem problem) {
        if (problem.getTestCases() != null) {
            problem.getTestCases().forEach(tc -> tc.setCodingProblem(problem));
        }
        return problemRepository.save(problem);
    }

    public List<CodingProblem> getProblemsByTopic(Long topicId) {
        return problemRepository.findByTopicId(topicId);
    }

    @Transactional
    public CodingProblem updateProblem(Long id, CodingProblem problemDetails) {
        CodingProblem problem = getProblemById(id);
        
        problem.setTitle(problemDetails.getTitle());
        problem.setDescription(problemDetails.getDescription());
        problem.setDifficulty(problemDetails.getDifficulty());
        problem.setMethodName(problemDetails.getMethodName());
        problem.setReturnType(problemDetails.getReturnType());
        problem.setParameters(problemDetails.getParameters());
        problem.setVisibleInput(problemDetails.getVisibleInput());
        problem.setVisibleOutput(problemDetails.getVisibleOutput());
        problem.setCategory(problemDetails.getCategory());
        problem.setTopic(problemDetails.getTopic());
        problem.setType(problemDetails.getType());
        problem.setSubtype(problemDetails.getSubtype());

        // Note: Test cases are usually updated via the dedicated TestcaseController / TestcaseService
        // But we ensure the problem object remains consistent.
        
        return problemRepository.save(problem);
    }

    @Transactional
    @SuppressWarnings("null")
    public void deleteProblem(Long id) {
        // Use optimized batch deletion for test cases
        testcaseService.deleteAllTestCases(id);
        
        // Delete the problem itself
        problemRepository.deleteById(id);
    }
}
