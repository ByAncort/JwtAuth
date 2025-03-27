package com.app.authjwt.User.service;

import com.app.authjwt.User.Model.User;
import com.app.authjwt.User.Repository.UserRepository;
import com.app.authjwt.config.jwt.JwtUtils;
import com.app.authjwt.dto.PermissionDto;
import com.app.authjwt.dto.RoleDto;
import com.app.authjwt.dto.UserDto;
import com.app.authjwt.payload.request.RegisterRequest;
import org.apache.catalina.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    private Mapper mapper;

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .usermane(user.getUsername())
                .Email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> RoleDto.builder()
                                .name(role.getName())
                                .permissions(role.getPermissions().stream()
                                        .map(perm -> PermissionDto.builder()
                                                .name(perm.getName())
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .enabled(user.isEnabled())
                .build();
    }

    public UserDto getLoggedInUser() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();

                return userRepository.findByUsername(username)
                        .map(this::convertToDto) // Asume que tienes un método convertToDto
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            } else if (principal instanceof String) {
                // Handle case where principal is just a String (username)
                return userRepository.findByUsername((String) principal)
                        .map(this::convertToDto)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
