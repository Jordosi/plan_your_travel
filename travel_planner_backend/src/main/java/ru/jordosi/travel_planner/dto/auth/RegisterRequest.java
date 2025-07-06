package ru.jordosi.travel_planner.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ru.jordosi.travel_planner.common.ValidPassword;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @ValidPassword String password
) {}
