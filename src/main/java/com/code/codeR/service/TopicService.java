package com.code.codeR.service;

import com.code.codeR.model.SkillCategory;
import com.code.codeR.model.Topic;
import com.code.codeR.repository.SkillCategoryRepository;
import com.code.codeR.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final SkillCategoryRepository categoryRepository;

    public List<Topic> getTopicsByCategoryId(Long categoryId) {
        return topicRepository.findByCategoryId(categoryId);
    }

    @SuppressWarnings("null")
    public Topic createTopic(Topic topic) {
        // Ensure category is fetched or valid
        if (topic.getCategory() != null && topic.getCategory().getId() != null) {
            SkillCategory category = categoryRepository.findById(topic.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            topic.setCategory(category);
        }
        return topicRepository.save(topic);
    }

    @SuppressWarnings("null")
    public Topic updateTopic(Long id, Topic topicDetails) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        topic.setName(topicDetails.getName());
        topic.setDescription(topicDetails.getDescription());
        
        if (topicDetails.getCategory() != null && topicDetails.getCategory().getId() != null) {
            SkillCategory category = categoryRepository.findById(topicDetails.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            topic.setCategory(category);
        }
        
        return topicRepository.save(topic);
    }

    @SuppressWarnings("null")
    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }
}
