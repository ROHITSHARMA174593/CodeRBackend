package com.code.codeR.controller;

import com.code.codeR.model.Topic;
import com.code.codeR.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@CrossOrigin
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Topic>> getTopicsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(topicService.getTopicsByCategoryId(categoryId));
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        return ResponseEntity.ok(topicService.createTopic(topic));
    }
}
