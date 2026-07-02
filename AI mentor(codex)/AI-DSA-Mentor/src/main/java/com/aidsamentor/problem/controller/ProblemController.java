package com.aidsamentor.problem.controller;

import com.aidsamentor.common.dto.ApiResponse;
import com.aidsamentor.problem.dto.ProblemRequest;
import com.aidsamentor.problem.dto.ProblemResponse;
import com.aidsamentor.problem.entity.Difficulty;
import com.aidsamentor.problem.service.ProblemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/problems")
@Tag(name = "Problems", description = "Protected problem management endpoints.")
@SecurityRequirement(name = "bearerAuth")
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProblemResponse>> createProblem(@Valid @RequestBody ProblemRequest request) {
        ProblemResponse response = problemService.createProblem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Problem created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProblemResponse>> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody ProblemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Problem updated successfully", problemService.updateProblem(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.ok(ApiResponse.success("Problem deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProblemResponse>>> getAllProblems() {
        return ResponseEntity.ok(ApiResponse.success("Problems fetched successfully", problemService.getAllProblems()));
    }

    @GetMapping("/search/tag")
    public ResponseEntity<ApiResponse<List<ProblemResponse>>> searchByTag(@RequestParam String tag) {
        return ResponseEntity.ok(ApiResponse.success("Problems fetched successfully", problemService.searchByTag(tag)));
    }

    @GetMapping("/search/difficulty")
    public ResponseEntity<ApiResponse<List<ProblemResponse>>> searchByDifficulty(@RequestParam Difficulty difficulty) {
        return ResponseEntity.ok(ApiResponse.success("Problems fetched successfully", problemService.searchByDifficulty(difficulty)));
    }
}
