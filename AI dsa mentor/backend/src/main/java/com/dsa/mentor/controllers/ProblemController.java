package com.dsa.mentor.controllers;

import com.dsa.mentor.entities.Difficulty;
import com.dsa.mentor.entities.Problem;
import com.dsa.mentor.services.ProblemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @GetMapping
    public ResponseEntity<List<Problem>> getAllProblems(
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String tag) {
        if (difficulty != null) {
            return ResponseEntity.ok(problemService.getProblemsByDifficulty(difficulty));
        } else if (tag != null && !tag.isBlank()) {
            return ResponseEntity.ok(problemService.getProblemsByTag(tag));
        }
        return ResponseEntity.ok(problemService.getAllProblems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Problem> getProblemById(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getProblemById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Problem> createProblem(@Valid @RequestBody Problem problem) {
        return ResponseEntity.ok(problemService.createProblem(problem));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Problem> updateProblem(@PathVariable Long id, @Valid @RequestBody Problem problemDetails) {
        return ResponseEntity.ok(problemService.updateProblem(id, problemDetails));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
