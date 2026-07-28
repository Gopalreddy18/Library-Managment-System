package com.dsa.mentor.controllers;

import com.dsa.mentor.dtos.InterviewRespondRequest;
import com.dsa.mentor.dtos.InterviewStartRequest;
import com.dsa.mentor.entities.MockInterview;
import com.dsa.mentor.entities.User;
import com.dsa.mentor.security.services.UserDetailsImpl;
import com.dsa.mentor.services.MockInterviewService;
import com.dsa.mentor.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/interview")
public class MockInterviewController {

    @Autowired
    private MockInterviewService mockInterviewService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<MockInterview>> getMyInterviews(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(mockInterviewService.getUserInterviews(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MockInterview> getInterviewDetails(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        MockInterview interview = mockInterviewService.getInterviewById(id);
        if (!interview.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(interview);
    }

    @PostMapping("/start")
    public ResponseEntity<MockInterview> startInterview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody InterviewStartRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        MockInterview interview = mockInterviewService.startInterview(user, request.getMode());
        return ResponseEntity.ok(interview);
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<MockInterview> respondToInterview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestBody InterviewRespondRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        MockInterview interview = mockInterviewService.getInterviewById(id);
        if (!interview.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).build();
        }
        MockInterview updated = mockInterviewService.respondToInterview(id, request.getResponse());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<MockInterview> submitInterview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        MockInterview interview = mockInterviewService.getInterviewById(id);
        if (!interview.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).build();
        }
        MockInterview completed = mockInterviewService.submitInterview(id);
        return ResponseEntity.ok(completed);
    }
}
