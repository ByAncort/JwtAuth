package com.app.authjwt.auth;

import com.app.authjwt.User.Model.Role;
import com.app.authjwt.User.Model.User;
import com.app.authjwt.User.Repository.RoleRepository;
import com.app.authjwt.User.Repository.UserRepository;
import com.app.authjwt.dto.payload.request.LoginRequest;
import com.app.authjwt.dto.payload.request.RegisterRequest;
import com.app.authjwt.dto.payload.response.AuthResponse;
import com.app.authjwt.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtService;
    private final AuthenticationManager authenticationManager;

    
    @Value("${auth.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    public AuthResponse login(LoginRequest request) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        
        SecurityContextHolder.getContext().setAuthentication(authentication);

        
        User user = (User) authentication.getPrincipal();

        
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(userRole))
                .enabled(true)
                .build();

        userRepository.save(user);

        
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}