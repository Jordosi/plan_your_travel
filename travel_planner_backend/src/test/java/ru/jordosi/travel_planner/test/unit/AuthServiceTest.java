package ru.jordosi.travel_planner.test.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.jordosi.travel_planner.dto.auth.AuthRequest;
import ru.jordosi.travel_planner.dto.auth.AuthResponse;
import ru.jordosi.travel_planner.dto.auth.RegisterRequest;
import ru.jordosi.travel_planner.dto.user.UserResponse;
import ru.jordosi.travel_planner.model.User;
import ru.jordosi.travel_planner.security.auth.AuthController;
import ru.jordosi.travel_planner.security.auth.AuthService;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerShouldReturnAuthResponse() {
        RegisterRequest registerRequest = new RegisterRequest("abcd@mail.com", "Aa12345678");

        UserResponse userResponse = new UserResponse(
                1L, "abcd@mail.com", "abcd", "John", "Doe",
                "http://avatar.com/1.png", "RU", Set.of("tag1", "tag2"),
                User.Role.USER, true
        );

        AuthResponse expectedResponse = new AuthResponse("token123", userResponse);

        when(authService.register(registerRequest)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> actualResponse = authController.register(registerRequest);

        assertThat(actualResponse.getBody()).isEqualTo(expectedResponse);
        assertThat(actualResponse.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    void loginShouldReturnAuthResponse() {
        AuthRequest request = new AuthRequest("abcd@mail.com", "Aa12345678");
        UserResponse userResponse = new UserResponse(
                1L, "abcd@mail.com", "abcd", "John", "Doe",
                "http://avatar.com/1.png", "RU", Set.of("tag1", "tag2"),
                User.Role.USER, true
        );

        AuthResponse expectedResponse = new AuthResponse("token123", userResponse);

        when(authService.authenticate(request)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> actualResponse = authController.login(request);
        assertThat(actualResponse.getBody()).isEqualTo(expectedResponse);
        assertThat(actualResponse.getStatusCode().value()).isEqualTo(200);
    }
}