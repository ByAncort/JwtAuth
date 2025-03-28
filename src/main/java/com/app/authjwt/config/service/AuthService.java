package com.app.authjwt.config.service;

import com.app.authjwt.User.Model.Role;
import com.app.authjwt.User.Model.User;
import com.app.authjwt.User.Repository.RoleRepository;
import com.app.authjwt.User.Repository.UserRepository;
import com.app.authjwt.config.jwt.JwtUtils;
import com.app.authjwt.payload.request.LoginRequest;
import com.app.authjwt.payload.request.RegisterRequest;
import com.app.authjwt.payload.response.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;



@Service

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    private  RoleRepository roleRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  JwtUtils jwtService;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Value("${auth.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
        UserDetails user=userRepository.findByUsername(request.getUsername()).orElseThrow(()
                 -> new UsernameNotFoundException("User not found with username: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + jwtExpirationMs);

        String token = jwtService.getToken(user);

        return AuthResponse.builder()
                .issuedAt(issuedAt)
                .expiration(expiration)
                .token(token)
                .build();
//        AuthResponse tokenResponse = new AuthResponse( issuedAt, expiration,token);

//        AuthResponse response = new AuthResponse();
//        response.setToken(tokenResponse.getToken());
//        response.setIssuedAt(tokenResponse.getIssuedAt());
//        response.setExpiration(tokenResponse.getExpiration());
//        return tokenResponse;
    }

    public AuthResponse register(RegisterRequest request) {


        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        User user= User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(userRole))
                .build();




        userRepository.save(user);
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + jwtExpirationMs);
        String token = jwtService.getToken(user);

        return AuthResponse.builder()
                .issuedAt(issuedAt)
                .expiration(expiration)
                .token(token)
                .build();



    }
}
