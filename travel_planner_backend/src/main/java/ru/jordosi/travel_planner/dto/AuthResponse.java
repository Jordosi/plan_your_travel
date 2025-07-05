package ru.jordosi.travel_planner.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthResponse(@NotBlank String token, UserResponse user) {}
