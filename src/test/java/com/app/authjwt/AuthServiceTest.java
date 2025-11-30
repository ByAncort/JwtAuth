package com.app.authjwt;

import com.app.authjwt.User.Model.Role;
import com.app.authjwt.User.Model.User;
import com.app.authjwt.User.Repository.RoleRepository;
import com.app.authjwt.User.Repository.UserRepository;
import com.app.authjwt.auth.AuthService;
import com.app.authjwt.dto.payload.request.LoginRequest;
import com.app.authjwt.dto.payload.request.RegisterRequest;
import com.app.authjwt.dto.payload.response.AuthResponse;
import com.app.authjwt.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        
        
        
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 3600000L);
    }

    @Test
    @DisplayName("Login: Debería retornar token cuando las credenciales son válidas")
    void login_ShouldReturnToken_WhenCredentialsValid() {
        
        LoginRequest request = new LoginRequest("testuser", "password");
        User mockUser = User.builder().username("testuser").build();

        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        
        when(authentication.getPrincipal()).thenReturn(mockUser);

        
        when(jwtService.generateToken(mockUser)).thenReturn("jwt-token-valido");

        
        

        
        AuthResponse response = authService.login(request);

        
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-valido");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("Register: Debería guardar usuario y retornar token")
    void register_ShouldSaveUserAndReturnToken() {
        
        RegisterRequest request = new RegisterRequest("newUser", "email@test.com", "pass123");
        Role mockRole = Role.builder().name("ROLE_USER").build();
        User mockSavedUser = User.builder().username("newUser").roles(Collections.singleton(mockRole)).build();

        
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token-nuevo");

        
        AuthResponse response = authService.register(request);

        
        assertThat(response.getToken()).isEqualTo("jwt-token-nuevo");
        verify(userRepository).save(any(User.class)); 
    }

    @Test
    @DisplayName("Register: Debería lanzar excepción si el username ya existe")
    void register_ShouldThrowException_WhenUsernameExists() {
        
        RegisterRequest request = new RegisterRequest("existingUser", "email@test.com", "pass");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });

        assertThat(exception.getMessage()).contains("Username is already taken");
        verify(userRepository, never()).save(any()); 
    }
}