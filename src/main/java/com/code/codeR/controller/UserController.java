package com.code.codeR.controller;

import com.code.codeR.model.UserProgress;
import com.code.codeR.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<UserProgress> getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.getUserProgress(email));
    }

    @PostMapping("/progress")
    public ResponseEntity<UserProgress> updateProgress(@RequestParam(defaultValue = "0") int score, 
                                                       @RequestParam(defaultValue = "false") boolean problemSolved) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(dashboardService.updateProgress(email, score, problemSolved));
    }
}
