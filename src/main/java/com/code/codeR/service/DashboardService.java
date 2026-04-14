package com.code.codeR.service;

import com.code.codeR.model.User;
import com.code.codeR.model.UserProgress;
import com.code.codeR.repository.UserProgressRepository;
import com.code.codeR.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserProgressRepository progressRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserProgress getUserProgress(String email) {
        return progressRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    // We still need the user if progress doesn't exist
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    
                    UserProgress newProgress = new UserProgress();
                    newProgress.setUser(user);
                    newProgress.setQuizzesAttempted(0);
                    newProgress.setProblemsSolved(0);
                    return progressRepository.save(newProgress);
                });
    }

    @Transactional
    @SuppressWarnings("null")
    public UserProgress updateProgress(String email, int quizScoreToAdd, boolean problemSolved) {
        UserProgress progress = getUserProgress(email);
        
        if (quizScoreToAdd > 0) {
            progress.setQuizzesAttempted(progress.getQuizzesAttempted() + 1);
        }
        
        if (problemSolved) {
            progress.setProblemsSolved(progress.getProblemsSolved() + 1);
        }
        
        return progressRepository.save(progress);
    }
}
