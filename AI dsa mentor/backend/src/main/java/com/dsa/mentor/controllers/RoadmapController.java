package com.dsa.mentor.controllers;

import com.dsa.mentor.dtos.RoadmapRequest;
import com.dsa.mentor.entities.Roadmap;
import com.dsa.mentor.entities.User;
import com.dsa.mentor.security.services.UserDetailsImpl;
import com.dsa.mentor.services.RoadmapService;
import com.dsa.mentor.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/roadmap")
public class RoadmapController {

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public ResponseEntity<Roadmap> getCurrentRoadmap(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        Optional<Roadmap> roadmapOpt = roadmapService.getLatestRoadmap(user);
        return roadmapOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/generate")
    public ResponseEntity<Roadmap> generateRoadmap(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody RoadmapRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        Roadmap roadmap = roadmapService.generateRoadmap(
                user,
                request.getTargetCompany(),
                request.getCurrentSkillLevel(),
                request.getWeakTopics()
        );
        return ResponseEntity.ok(roadmap);
    }
}
