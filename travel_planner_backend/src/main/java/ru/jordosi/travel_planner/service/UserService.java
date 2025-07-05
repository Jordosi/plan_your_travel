package ru.jordosi.travel_planner.service;

import org.springframework.transaction.annotation.Transactional;
import ru.jordosi.travel_planner.dto.CreateUserRequest;
import ru.jordosi.travel_planner.dto.UpdateUserRequest;
import ru.jordosi.travel_planner.dto.UserResponse;
import ru.jordosi.travel_planner.model.User;

public interface UserService {
    @Transactional
    UserResponse createUser(CreateUserRequest request);

    @Transactional(readOnly = true)
    UserResponse findUserById(Long id);

    @Transactional(readOnly = true)
    UserResponse findUserByEmail(String email);

    @Transactional(readOnly = true)
    UserResponse updateUser(Long id, UpdateUserRequest request);

    @Transactional
    void deleteUser(Long id);

    @Transactional(readOnly = true)
    UserResponse getUserProfile(Long id);
}
