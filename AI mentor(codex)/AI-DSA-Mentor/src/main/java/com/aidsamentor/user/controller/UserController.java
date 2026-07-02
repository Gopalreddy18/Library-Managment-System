package com.aidsamentor.user.controller;

import com.aidsamentor.common.dto.ApiResponse;
import com.aidsamentor.user.dto.UserResponse;
import com.aidsamentor.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "Protected user endpoints.")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Current user fetched successfully", userService.getCurrentUser(principal)));
    }
}
