package com.code.codeR.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponse {
    private boolean success;
    private String message;
    private String output;
    private String expectedOutput;
    private String input;
    private int totalTestCases;
    private int passedTestCases;
}
