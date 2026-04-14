package com.code.codeR.controller;

import com.code.codeR.model.TestCase;
import com.code.codeR.service.TestcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/testcases")
@RequiredArgsConstructor
@CrossOrigin
public class TestcaseController {

    private final TestcaseService testcaseService;

    // Upload Testcase (Input + Output files)
    @PostMapping("/problem/{problemId}")
    public ResponseEntity<TestCase> uploadTestCase(
            @PathVariable Long problemId,
            @RequestParam("input") MultipartFile inputFile,
            @RequestParam("output") MultipartFile outputFile
    ) throws IOException {
        return ResponseEntity.ok(testcaseService.addTestCase(problemId, inputFile, outputFile));
    }

    // Replace Testcase (Delete all old ones, add new)
    @PutMapping("/problem/{problemId}")
    public ResponseEntity<TestCase> replaceTestCase(
            @PathVariable Long problemId,
            @RequestParam("input") MultipartFile inputFile,
            @RequestParam("output") MultipartFile outputFile
    ) throws IOException {
        return ResponseEntity.ok(testcaseService.replaceTestCase(problemId, inputFile, outputFile));
    }

    // Get all Testcases for a problem (Metadata only)
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<List<TestCase>> getTestCases(@PathVariable Long problemId) {
        return ResponseEntity.ok(testcaseService.getTestCasesByProblemId(problemId));
    }

    // Stream Input File
    @GetMapping("/{testCaseId}/input")
    @SuppressWarnings("null")
    public ResponseEntity<InputStreamResource> streamInputFile(@PathVariable Long testCaseId) {
        InputStream inputStream = testcaseService.getTestCaseFileStream(testCaseId, true);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"input.txt\"")
                .body(new InputStreamResource(inputStream));
    }

    // Stream Output File
    @GetMapping("/{testCaseId}/output")
    @SuppressWarnings("null")
    public ResponseEntity<InputStreamResource> streamOutputFile(@PathVariable Long testCaseId) {
        InputStream inputStream = testcaseService.getTestCaseFileStream(testCaseId, false);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"output.txt\"")
                .body(new InputStreamResource(inputStream));
    }

    // Delete Testcase
    @DeleteMapping("/{testCaseId}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long testCaseId) {
        testcaseService.deleteTestCase(testCaseId);
        return ResponseEntity.noContent().build();
    }
}
