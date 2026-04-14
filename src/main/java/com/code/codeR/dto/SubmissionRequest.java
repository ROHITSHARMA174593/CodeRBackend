package com.code.codeR.dto;

import lombok.Data;

@Data
public class SubmissionRequest {
    private Long problemId;
    private String code;
    private String language; // "java", "python", etc (currently only support java)
}
