package com.code.codeR.dto;

import com.code.codeR.model.CodingProblem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDTO {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private String type;
    private String subtype;
    private String categoryName;
    private boolean solved;

    public static ProblemDTO fromEntity(CodingProblem problem, boolean solved) {
        return ProblemDTO.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty())
                .type(problem.getType())
                .subtype(problem.getSubtype())
                .categoryName(problem.getCategory() != null ? problem.getCategory().getName() : null)
                .solved(solved)
                .build();
    }
}
