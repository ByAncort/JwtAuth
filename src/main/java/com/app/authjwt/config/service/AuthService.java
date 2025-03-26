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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
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



    public AuthResponse login(LoginRequest request) {
        return null;
    }

    public AuthResponse register(RegisterRequest request) {


        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRoles(Collections.singleton(userRole));


        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setToken(jwtService.getToken(user));
        return response;



    }
}
