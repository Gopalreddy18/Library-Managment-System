package com.dsa.mentor.controllers;

import com.dsa.mentor.dtos.SubmissionRequest;
import com.dsa.mentor.entities.Analysis;
import com.dsa.mentor.entities.Submission;
import com.dsa.mentor.entities.User;
import com.dsa.mentor.security.services.UserDetailsImpl;
import com.dsa.mentor.services.SubmissionService;
import com.dsa.mentor.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Submission> submitSolution(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody SubmissionRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        Submission submission = submissionService.submitSolution(
                user,
                request.getProblemId(),
                request.getSourceCode(),
                request.getLanguage()
        );
        return ResponseEntity.ok(submission);
    }

    @GetMapping
    public ResponseEntity<List<Submission>> getMySubmissions(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(submissionService.getUserSubmissions(user));
    }

    @GetMapping("/problem/{problemId}")
    public ResponseEntity<List<Submission>> getMySubmissionsForProblem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long problemId) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(submissionService.getSubmissionsByProblem(user, problemId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmissionDetails(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Submission submission = submissionService.getSubmissionById(id);
        // Ensure user can only view their own submissions
        if (!submission.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<Analysis> getSubmissionAnalysis(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Submission submission = submissionService.getSubmissionById(id);
        if (!submission.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).build();
        }
        Analysis analysis = submissionService.getAnalysisBySubmissionId(id);
        return ResponseEntity.ok(analysis);
    }
}
