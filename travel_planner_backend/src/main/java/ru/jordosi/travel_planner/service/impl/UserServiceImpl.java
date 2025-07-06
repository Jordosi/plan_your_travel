package ru.jordosi.travel_planner.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jordosi.travel_planner.dto.user.CreateUserRequest;
import ru.jordosi.travel_planner.dto.user.UpdateUserRequest;
import ru.jordosi.travel_planner.dto.user.UserResponse;
import ru.jordosi.travel_planner.exception.EmailAlreadyExistsException;
import ru.jordosi.travel_planner.exception.InvalidNationalityException;
import ru.jordosi.travel_planner.exception.UserNotFoundException;
import ru.jordosi.travel_planner.model.User;
import ru.jordosi.travel_planner.repository.UserRepository;
import ru.jordosi.travel_planner.service.NationalityService;
import ru.jordosi.travel_planner.service.UserService;
import ru.jordosi.travel_planner.utils.TagValidator;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NationalityService nationalityService;
    private final TagValidator tagValidator;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .username(request.email().split("@")[0])
                .firstName(request.firstName())
                .lastName(request.lastName())
                .nationality(request.nationality())
                .avatarURL("")
                .role(request.role() != null ? request.role() : User.Role.USER)
                .preferenceTags(request.preferenceTags())
                .enabled(true)
                .build();
        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());
        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (request.nationality() != null) {
            if (!nationalityService.validateNationality(request.nationality())) {
                throw new InvalidNationalityException("Nationality not found");
            }
            user.setNationality(request.nationality());
        }
        Optional.ofNullable(request.firstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(request.lastName()).ifPresent(user::setLastName);
        Optional.ofNullable(request.avatarURL()).ifPresent(user::setAvatarURL);
        Optional.ofNullable(request.preferredTags()).ifPresent(user::setPreferenceTags);

        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long id) {
        return mapToUserResponse(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserTags(Long id) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return user.getPreferenceTags();
    }

    @Override
    @Transactional
    public void addTags(Long id, Set<String> tags) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new  UserNotFoundException(id));
        tagValidator.validate(tags);
        user.getPreferenceTags().addAll(tags);
    }

    @Override
    @Transactional
    public void removeTags(Long id, Set<String> tags) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.getPreferenceTags().removeAll(tags);
    }

    private UserResponse mapToUserResponse(User savedUser) {
        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getAvatarURL(),
                savedUser.getNationality(),
                savedUser.getPreferenceTags(),
                savedUser.getRole(),
                savedUser.isEnabled()
        );
    }
}
