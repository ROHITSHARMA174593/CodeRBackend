package com.code.codeR.service;

import com.code.codeR.model.CodingProblem;
import com.code.codeR.model.TestCase;
import com.code.codeR.repository.CodingProblemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestcaseService {

    private final CodingProblemRepository codingProblemRepository;
    // Assuming you might need a TestCaseRepository if you want to manage them directly
    // If not present, I'll rely on cascading or create it.
    // For now, I'll assume it exists or I'll implement logic via CodingProblem if possible,
    // but direct repository is better for delete/find operations on TestCases.
    private final com.code.codeR.repository.TestCaseRepository testCaseRepository; 

    @SuppressWarnings("null")
    public TestCase addTestCase(Long problemId, MultipartFile inputFile, MultipartFile outputFile) throws IOException {
        CodingProblem problem = codingProblemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));

        // Read content as String instead of saving to disk
        String inputContent = new String(inputFile.getBytes());
        String outputContent = new String(outputFile.getBytes());

        // Automatically extract first 2 lines for visible test cases
        String visibleInput = extractFirstTwoLines(inputFile);
        String visibleOutput = extractFirstTwoLines(outputFile);
        
        problem.setVisibleInput(visibleInput);
        problem.setVisibleOutput(visibleOutput);
        codingProblemRepository.save(problem);

        TestCase testCase = new TestCase();
        testCase.setInput(inputFile.getOriginalFilename());  // Store original name for reference
        testCase.setExpectedOutput(outputFile.getOriginalFilename()); // Store original name for reference
        testCase.setInputContent(inputContent);
        testCase.setExpectedOutputContent(outputContent);
        testCase.setCodingProblem(problem);
        
        return testCaseRepository.save(testCase);
    }

    private String extractFirstTwoLines(MultipartFile file) {
        if (file == null || file.isEmpty()) return "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < 2) {
                if (count > 0) sb.append("\n");
                sb.append(line);
                count++;
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    @SuppressWarnings("null")
    public TestCase addHiddenTestCase(Long problemId, MultipartFile input, MultipartFile output) throws IOException {
        CodingProblem problem = codingProblemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        if (input.isEmpty() || output.isEmpty()) {
             throw new IllegalArgumentException("Input and Output files cannot be empty");
        }

        // Read content as String instead of saving to disk
        String inputContent = new String(input.getBytes());
        String outputContent = new String(output.getBytes());

        // Automatically extract first 2 lines for visible test cases
        String visibleInput = extractFirstTwoLines(input);
        String visibleOutput = extractFirstTwoLines(output);
        
        problem.setVisibleInput(visibleInput);
        problem.setVisibleOutput(visibleOutput);
        codingProblemRepository.save(problem);

        TestCase testCase = new TestCase();
        testCase.setInput(input.getOriginalFilename()); 
        testCase.setExpectedOutput(output.getOriginalFilename());
        testCase.setInputContent(inputContent);
        testCase.setExpectedOutputContent(outputContent);
        testCase.setCodingProblem(problem);

        return testCaseRepository.save(testCase);
    }
    
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public List<TestCase> getTestCasesByProblemId(Long problemId) {
       CodingProblem problem = codingProblemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));
       return problem.getTestCases();
    }
    
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public InputStream getTestCaseFileStream(Long testCaseId, boolean isInput) {
        TestCase testCase = testCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new RuntimeException("Testcase not found"));
        
        String content = isInput ? testCase.getInputContent() : testCase.getExpectedOutputContent();
        if (content == null) content = "";
        return new java.io.ByteArrayInputStream(content.getBytes());
    }

    @Transactional
    @SuppressWarnings("null")
    public void deleteTestCase(Long testCaseId) {
        testCaseRepository.deleteById(testCaseId);
    }

    @Transactional
    public void deleteAllTestCases(Long problemId) {
        // Batch delete from DB in one hit
        testCaseRepository.deleteByCodingProblemId(problemId);
    }

    @Transactional
    // @SuppressWarnings("null")
    public TestCase replaceTestCase(Long problemId, MultipartFile input, MultipartFile output) throws IOException {
        deleteAllTestCases(problemId);
        return addTestCase(problemId, input, output);
    }
}
