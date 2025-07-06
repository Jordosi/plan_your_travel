package ru.jordosi.travel_planner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jordosi.travel_planner.dto.user.UpdateUserRequest;
import ru.jordosi.travel_planner.dto.user.UserResponse;
import ru.jordosi.travel_planner.model.User;
import ru.jordosi.travel_planner.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest updateUser, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.updateUser(user.getId(), updateUser));
    }
}
