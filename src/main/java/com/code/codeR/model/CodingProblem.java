package com.code.codeR.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodingProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String difficulty; // Easy, Medium, Hard

    @Column
    private String methodName;

    @Column
    private String returnType;

    @Column(columnDefinition = "TEXT")
    private String visibleInput;

    @Column(columnDefinition = "TEXT")
    private String visibleOutput;

    @Column(columnDefinition = "TEXT")
    private String parameters; // JSON format e.g. [{"type":"int","name":"a"}, ...]

    @Column
    private String type; // array, string, linkedlist

    @Column
    private String subtype; // singly, doubly, circular, doubly_circular

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private SkillCategory category;

    @OneToMany(mappedBy = "codingProblem", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<TestCase> testCases = new java.util.ArrayList<>();
}
