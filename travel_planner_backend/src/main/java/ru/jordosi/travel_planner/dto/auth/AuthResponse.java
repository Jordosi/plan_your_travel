package ru.jordosi.travel_planner.dto.auth;

import jakarta.validation.constraints.NotBlank;
import ru.jordosi.travel_planner.dto.user.UserResponse;

public record AuthResponse(@NotBlank String token, UserResponse user) {}
