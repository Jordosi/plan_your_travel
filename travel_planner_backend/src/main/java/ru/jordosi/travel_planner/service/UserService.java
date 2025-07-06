package ru.jordosi.travel_planner.service;

import org.springframework.transaction.annotation.Transactional;
import ru.jordosi.travel_planner.dto.user.CreateUserRequest;
import ru.jordosi.travel_planner.dto.user.UpdateUserRequest;
import ru.jordosi.travel_planner.dto.user.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse findUserById(Long id);

    UserResponse findUserByEmail(String email);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse getUserProfile(Long id);
}
