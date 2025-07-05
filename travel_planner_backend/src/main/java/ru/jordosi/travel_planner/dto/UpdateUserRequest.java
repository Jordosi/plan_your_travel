package ru.jordosi.travel_planner.dto;

import java.util.Set;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String nationality,
        String avatarURL,
        Set<String> preferredTags,
        boolean enabled
) {}
