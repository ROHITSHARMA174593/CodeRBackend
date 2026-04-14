package com.code.codeR.service;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class CodeSecurityValidator {

    private static final List<String> BLOCKED_KEYWORDS = List.of(
            "System.exit",
            "Runtime.getRuntime",
            "ProcessBuilder",
            "java.io.File",
            "java.nio.file",
            "java.net",
            "Thread",
            "Reflection"
    );

    public void validate(String code) {
        for (String keyword : BLOCKED_KEYWORDS) {
            if (code.contains(keyword)) {
                throw new SecurityException("Security Violation: usage of '" + keyword + "' is not allowed.");
            }
        }
        
        // Basic check for infinite loops or suspicious patterns could go here
    }
}
