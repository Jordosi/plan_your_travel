package ru.jordosi.travel_planner.dto;

import ru.jordosi.travel_planner.model.User;

import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String username,
        String firstName,
        String lastName,
        String avatarUrl,
        String nationality,
        Set<String> preferenceTags,
        User.Role role,
        boolean enabled
) {}
