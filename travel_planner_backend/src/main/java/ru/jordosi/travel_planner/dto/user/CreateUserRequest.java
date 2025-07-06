package ru.jordosi.travel_planner.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import ru.jordosi.travel_planner.common.ValidPassword;
import ru.jordosi.travel_planner.model.User;

import java.util.Set;

public record CreateUserRequest(
        @NotNull @NotBlank @Email String email,
        @NotNull @NotBlank @ValidPassword String password,
        String firstName,
        String lastName,
        @Length(max = 2) String nationality,
        Set<String> preferenceTags,
        @NotNull User.Role role
) {}
