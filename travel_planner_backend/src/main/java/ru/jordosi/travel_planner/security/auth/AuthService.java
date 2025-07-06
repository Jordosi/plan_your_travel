package ru.jordosi.travel_planner.security.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.jordosi.travel_planner.dto.auth.AuthRequest;
import ru.jordosi.travel_planner.dto.auth.AuthResponse;
import ru.jordosi.travel_planner.dto.auth.RegisterRequest;
import ru.jordosi.travel_planner.dto.user.CreateUserRequest;
import ru.jordosi.travel_planner.dto.user.UserResponse;
import ru.jordosi.travel_planner.exception.EmailAlreadyExistsException;
import ru.jordosi.travel_planner.model.User;
import ru.jordosi.travel_planner.repository.UserRepository;
import ru.jordosi.travel_planner.security.jwt.JwtService;
import ru.jordosi.travel_planner.service.UserService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Transactional
    public AuthResponse register (RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new EmailAlreadyExistsException(registerRequest.email());
        }

        CreateUserRequest request = new CreateUserRequest(
                registerRequest.email(),
                registerRequest.password(),
                "",
                "",
                "",
                Collections.emptySet(),
                User.Role.USER
        );

        UserResponse response = userService.createUser(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(response.username());
        var jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponse(jwtToken, response);
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );

        UserResponse response = userService.findUserByEmail(authRequest.email());

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.email());

        var jwtToken = jwtService.generateToken(userDetails);
        return new AuthResponse(jwtToken, response);
    }
}
