package com.dsa.mentor.controllers;

import com.dsa.mentor.entities.User;
import com.dsa.mentor.entities.UserStatistics;
import com.dsa.mentor.security.services.UserDetailsImpl;
import com.dsa.mentor.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        // Clean password before returning
        user.setPassword("");
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody User profileDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User updated = userService.updateProfile(userDetails.getId(), profileDetails);
        updated.setPassword("");
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatistics> getMyStats(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userDetails.getId());
        UserStatistics stats = userService.getOrCreateUserStatistics(user);
        return ResponseEntity.ok(stats);
    }
}
