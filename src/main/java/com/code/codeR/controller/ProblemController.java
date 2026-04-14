package com.code.codeR.controller;

import com.code.codeR.dto.SubmissionRequest;
import com.code.codeR.model.CodingProblem;
import com.code.codeR.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@CrossOrigin
public class ProblemController {

    private final ProblemService problemService;
    private final com.code.codeR.service.CodeExecutionService codeExecutionService;

    @GetMapping
    public ResponseEntity<List<com.code.codeR.dto.ProblemDTO>> getAllProblems(Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(problemService.getAllProblemsWithStatus(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodingProblem> getProblemById(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getProblemById(id));
    }

    @PostMapping(value = "/{id}/submit-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter submitProblemStream(@PathVariable Long id, @RequestBody SubmissionRequest request, Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        SseEmitter emitter = new SseEmitter(120000L); // 2 mins timeout
        
        Thread executionThread = new Thread(() -> {
            try {
                codeExecutionService.submitCodeStreaming(id, request.getCode(), email, response -> {
                    try {
                        if (response != null) {
                            emitter.send(response);
                        }
                    } catch (IOException e) {
                        // Client closed connection
                    }
                });
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> {
            emitter.complete();
            executionThread.interrupt();
        });
        emitter.onCompletion(() -> executionThread.interrupt());

        executionThread.start();
        return emitter;
    }

    @PostMapping(value = "/{id}/run-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter runProblemStream(@PathVariable Long id, @RequestBody SubmissionRequest request, Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        SseEmitter emitter = new SseEmitter(60000L); // 1 min timeout
        
        Thread executionThread = new Thread(() -> {
            try {
                codeExecutionService.runVisibleTestStreaming(id, request.getCode(), email, response -> {
                    try {
                        if (response != null) {
                            emitter.send(response);
                        }
                    } catch (IOException e) {
                        // Client closed connection
                    }
                });
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> {
            emitter.complete();
            executionThread.interrupt();
        });
        emitter.onCompletion(() -> executionThread.interrupt());

        executionThread.start();
        return emitter;
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<com.code.codeR.dto.ProblemDTO>> getProblemsByCategory(@PathVariable Long categoryId, Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(problemService.getProblemsByCategoryWithStatus(categoryId, email));
    }

    @PostMapping
    public ResponseEntity<CodingProblem> createProblem(@RequestBody CodingProblem problem) {
        return ResponseEntity.ok(problemService.createProblem(problem));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<CodingProblem>> getProblemsByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(problemService.getProblemsByTopic(topicId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CodingProblem> updateProblem(@PathVariable Long id, @RequestBody CodingProblem problem) {
        return ResponseEntity.ok(problemService.updateProblem(id, problem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
