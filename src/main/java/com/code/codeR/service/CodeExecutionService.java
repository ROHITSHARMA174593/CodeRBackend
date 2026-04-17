package com.code.codeR.service;

import com.code.codeR.dto.SubmissionResponse;
import com.code.codeR.model.CodingProblem;
import com.code.codeR.model.TestCase;
import com.code.codeR.model.User;
import com.code.codeR.model.UserProgress;
import com.code.codeR.repository.CodingProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    private final CodingProblemRepository problemRepository;
    private final com.code.codeR.repository.UserRepository userRepository;
    private final com.code.codeR.repository.UserProgressRepository userProgressRepository;
    private final CodeSecurityValidator securityValidator;
    private final MainMethodGenerator mainMethodGenerator;

    @Transactional(readOnly = true)
    public SubmissionResponse runVisibleTest(Long problemId, String userCode, String userEmail) {
        // Use optimized fetch to get problem and test cases in one hit if needed, 
        // though visible tests don't strictly need hidden test cases, 
        // using the same optimized method is fine.
        CodingProblem problem = problemRepository.findByIdWithTestCases(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        try {
            securityValidator.validate(userCode);
        } catch (SecurityException e) {
            return SubmissionResponse.builder().success(false).message(e.getMessage()).build();
        }

        return executeInternally(problem, userCode, userEmail, true);
    }

    @Transactional(readOnly = true)
    public SubmissionResponse submitCode(Long problemId, String userCode, String userEmail) {
        CodingProblem problem = problemRepository.findByIdWithTestCases(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        try {
            securityValidator.validate(userCode);
        } catch (SecurityException e) {
            return SubmissionResponse.builder().success(false).message(e.getMessage()).build();
        }

        return executeInternally(problem, userCode, userEmail, false);
    }

    private SubmissionResponse executeInternally(CodingProblem problem, String userCode, String userEmail, boolean visibleOnly) {
        final SubmissionResponse[] finalResult = new SubmissionResponse[1];
        executeInternallyStreaming(problem, userCode, userEmail, visibleOnly, response -> {
            // Final result is either success=true or an error message (not "Running...")
            if (response.isSuccess() || (response.getMessage() != null && !response.getMessage().equals("Running..."))) {
                finalResult[0] = response;
            }
        });
        return finalResult[0];
    }

    public void submitCodeStreaming(Long problemId, String userCode, String userEmail, java.util.function.Consumer<SubmissionResponse> emitter) {
        CodingProblem problem = problemRepository.findByIdWithTestCases(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        try {
            securityValidator.validate(userCode);
        } catch (SecurityException e) {
            emitter.accept(SubmissionResponse.builder().success(false).message(e.getMessage()).build());
            return;
        }

        executeInternallyStreaming(problem, userCode, userEmail, false, emitter);
    }

    public void runVisibleTestStreaming(Long problemId, String userCode, String userEmail, java.util.function.Consumer<SubmissionResponse> emitter) {
        CodingProblem problem = problemRepository.findByIdWithTestCases(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        try {
            securityValidator.validate(userCode);
        } catch (SecurityException e) {
            emitter.accept(SubmissionResponse.builder().success(false).message(e.getMessage()).build());
            return;
        }

        executeInternallyStreaming(problem, userCode, userEmail, true, emitter);
    }

    private void executeInternallyStreaming(CodingProblem problem, String userCode, String userEmail, boolean visibleOnly, java.util.function.Consumer<SubmissionResponse> emitter) {
        String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "codeR_" + UUID.randomUUID();
        File directory = new File(tempDir);
        
        System.out.println("--- DIAGNOSTIC: Starting execution in " + tempDir);
        System.out.println("--- DIAGNOSTIC: PATH=" + System.getenv("PATH"));
        System.out.println("--- DIAGNOSTIC: JAVA_HOME=" + System.getenv("JAVA_HOME"));
        
        String javacPath = "javac";
        // Try common locations if raw 'javac' fails or for robustness
        String[] commonPaths = {"/usr/bin/javac", "/usr/local/bin/javac", "/opt/java/openjdk/bin/javac"};
        for (String p : commonPaths) {
            if (new File(p).exists()) {
                javacPath = p;
                System.out.println("--- DIAGNOSTIC: Found javac at " + p);
                break;
            }
        }
        if (!directory.mkdirs()) {
             emitter.accept(SubmissionResponse.builder().success(false).message("Internal Error").build());
             return;
        }

        try {
            // 1. Generate & Compile
            String mainCode = mainMethodGenerator.generateMainClass(problem);
            String solutionCode = userCode.contains("class Solution") ? userCode : "public class Solution {\n" + userCode + "\n}";
            Files.writeString(new File(directory, "Main.java").toPath(), mainCode);
            Files.writeString(new File(directory, "Solution.java").toPath(), solutionCode);

            ProcessBuilder compilePB = new ProcessBuilder(javacPath, "Main.java", "Solution.java");
            compilePB.directory(directory);
            compilePB.redirectErrorStream(true);
            Process compileProcess = compilePB.start();
            
            if (!compileProcess.waitFor(15, TimeUnit.SECONDS) || compileProcess.exitValue() != 0) {
                 String error = new String(compileProcess.getInputStream().readAllBytes());
                 emitter.accept(SubmissionResponse.builder().success(false).message("Compilation Failed").output(error.replace(tempDir, "")).build());
                 return;
            }

            // 2. Count Total (Visible + Hidden if Submission)
            int visibleCount = (int) (problem.getVisibleInput() != null ? 
                java.util.Arrays.stream(problem.getVisibleInput().split("\\R")).filter(s -> !s.trim().isEmpty()).count() : 0);
            
            int totalTestCases = visibleCount;
            if (!visibleOnly) {
                for (TestCase tc : problem.getTestCases()) {
                    if (tc.getInputContent() != null) {
                    try (BufferedReader reader = new BufferedReader(new java.io.StringReader(tc.getInputContent()))) {
                        totalTestCases += (int) reader.lines()
                            .map(String::trim)
                            .filter(s -> !s.trim().isEmpty()).count();
                    }
                }
            }
        }
            if (totalTestCases == 0) {
                emitter.accept(SubmissionResponse.builder().success(false).message("No test cases defined for this problem.").build());
                return;
            }

            // Send initial count
            emitter.accept(SubmissionResponse.builder()
                .totalTestCases(totalTestCases)
                .passedTestCases(0)
                .message("Initializing Test Cases...")
                .build());

            int passedTestCases = 0;
            
            // Phase 1: Visible Test Cases
            List<String> visibleInputs = problem.getVisibleInput() != null ? 
                java.util.Arrays.stream(problem.getVisibleInput().split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.trim().isEmpty())
                    .collect(java.util.stream.Collectors.toList()) :
                java.util.Collections.emptyList();

            List<String> visibleExpecteds = problem.getVisibleOutput() != null ? 
                java.util.Arrays.stream(problem.getVisibleOutput().split("\\R"))
                    .map(String::trim)
                    .filter(s -> !s.trim().isEmpty())
                    .collect(java.util.stream.Collectors.toList()) :
                java.util.Collections.emptyList();

            if (!visibleInputs.isEmpty()) {
                passedTestCases = runAndStream(directory, visibleInputs, visibleExpecteds, totalTestCases, 0, emitter, false, "Checking Visible Test Cases");
                if (passedTestCases < visibleInputs.size()) {
                    return; // Stop on first failure
                }
            }

            // Phase 2: Hidden Test Cases (Only if not visibleOnly)
            if (!visibleOnly) {
                for (TestCase tc : problem.getTestCases()) {
                    List<String> hInputs;
                    List<String> hExpecteds;
                    
                    String inputContent = tc.getInputContent() != null ? tc.getInputContent() : "";
                    String outputContent = tc.getExpectedOutputContent() != null ? tc.getExpectedOutputContent() : "";
                    
                    try (BufferedReader inR = new BufferedReader(new java.io.StringReader(inputContent));
                         BufferedReader exR = new BufferedReader(new java.io.StringReader(outputContent))) {
                        hInputs = inR.lines().map(String::trim).filter(s -> !s.trim().isEmpty()).collect(java.util.stream.Collectors.toList());
                        hExpecteds = exR.lines().map(String::trim).filter(s -> !s.trim().isEmpty()).collect(java.util.stream.Collectors.toList());
                    }
                    
                    int currentPassed = runAndStream(directory, hInputs, hExpecteds, totalTestCases, passedTestCases, emitter, true, "Checking Hidden Test Cases");
                    if (currentPassed < passedTestCases + hInputs.size()) {
                        return; // Stop on first failure
                    }
                    passedTestCases = currentPassed;
                }
            }

            if (passedTestCases == totalTestCases) {
                if (userEmail != null && !visibleOnly) {
                    updateUserProgress(userEmail, problem);
                }
                emitter.accept(SubmissionResponse.builder()
                        .success(true)
                        .message("Accepted")
                        .passedTestCases(passedTestCases)
                        .totalTestCases(totalTestCases)
                        .output("") 
                        .expectedOutput("")
                        .build());
            }

        } catch (Exception e) {
            emitter.accept(SubmissionResponse.builder().success(false).message("System Error: " + e.getMessage()).build());
        } finally {
            deleteDirectory(directory);
        }
    }

    private int runAndStream(File directory, List<String> inputs, List<String> expecteds, int total, int alreadyPassed, java.util.function.Consumer<SubmissionResponse> emitter, boolean isHidden, String statusPrefix) throws IOException, InterruptedException {
        String javaPath = "java";
        if (new File("/opt/java/openjdk/bin/java").exists()) javaPath = "/opt/java/openjdk/bin/java";
        else if (new File("/usr/bin/java").exists()) javaPath = "/usr/bin/java";

        ProcessBuilder pb = new ProcessBuilder(javaPath, "-cp", ".", "Main");
        pb.directory(directory);
        pb.redirectErrorStream(true); // MERGE stderr into stdout to capture all output/errors
        Process process = pb.start();

        // Write inputs in separate thread
        new Thread(() -> {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(process.getOutputStream()))) {
                for (String in : inputs) {
                    writer.println(in);
                }
                writer.flush();
            }
        }).start();

        int passedCount = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder currentCase = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("---CASE_END---")) {
                    String result = currentCase.toString().trim();
                    currentCase.setLength(0);
                    
                    String input = inputs.get(passedCount).trim();
                    String expected = (passedCount < expecteds.size()) ? expecteds.get(passedCount).trim() : "";
                    
                    // Super-Robust comparison: strip EVERYTHING except alphanumeric and signs
                    // This handles [1, 2] vs 1 2 vs 1, 2 vs [1,2] perfectly.
                    String normalizedResult = result.replaceAll("[^a-zA-Z0-9-]", "");
                    String normalizedExpected = expected.replaceAll("[^a-zA-Z0-9-]", "");

                    if (result.startsWith("RUNTIME_ERROR:")) {
                        emitter.accept(SubmissionResponse.builder()
                            .success(false)
                            .message("Oops! Runtime Error")
                            .input(input)
                            .output(result.replace("RUNTIME_ERROR:", "").trim())
                            .totalTestCases(total)
                            .passedTestCases(alreadyPassed + passedCount)
                            .build());
                        process.destroy();
                        return alreadyPassed + passedCount;
                    }

                    if (!normalizedResult.equals(normalizedExpected)) {
                        emitter.accept(SubmissionResponse.builder()
                            .success(false)
                            .message("Oops! Test Case Failed")
                            .input(input)
                            .output(result)
                            .expectedOutput(isHidden ? "Hidden" : expected)
                            .totalTestCases(total)
                            .passedTestCases(alreadyPassed + passedCount)
                            .build());
                        process.destroy();
                        return alreadyPassed + passedCount;
                    }
                    
                    passedCount++;
                    emitter.accept(SubmissionResponse.builder()
                        .totalTestCases(total)
                        .passedTestCases(alreadyPassed + passedCount)
                        .message(statusPrefix + "... (" + (alreadyPassed + passedCount) + "/" + total + ")")
                        .build());
                } else {
                    if (currentCase.length() > 0) currentCase.append("\n");
                    currentCase.append(line);
                }
            }
        }
        
        // Capture any remaining error stream (crashes not caught by Main.java)
        if (!process.isAlive() && process.exitValue() != 0) {
            String error = new String(process.getErrorStream().readAllBytes());
            if (!error.isEmpty()) {
                 emitter.accept(SubmissionResponse.builder().success(false).message("Runtime Crash").output(error).totalTestCases(total).passedTestCases(alreadyPassed + passedCount).build());
            }
        }

        process.waitFor(5, TimeUnit.SECONDS);
        return alreadyPassed + passedCount;
    }

    private void updateUserProgress(String email, CodingProblem problem) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update total solved count
        UserProgress progress = userProgressRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    UserProgress newProgress = new UserProgress(null, user, 0, 0);
                    return userProgressRepository.save(newProgress);
                });
        
        progress.setProblemsSolved(progress.getProblemsSolved() + 1);
        userProgressRepository.save(progress);

        // Record the specific problem as solved
        user.getSolvedProblems().add(problem);
        userRepository.save(user);
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        if (directoryToBeDeleted == null || !directoryToBeDeleted.exists()) return;
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
