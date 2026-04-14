package com.code.codeR.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String input;

    @Column(nullable = false)
    private String expectedOutput; // Stores S3 Key for hidden output

    @Column(nullable = false)
    private boolean isHidden = true; // Default to true as these are S3 keys now (hidden)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_problem_id")
    @JsonIgnore
    private CodingProblem codingProblem;
}
