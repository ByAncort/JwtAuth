package com.app.authjwt.controller;

import com.app.authjwt.User.Repository.UserRepository;
import com.app.authjwt.auth.AuthService;
import com.app.authjwt.dto.payload.request.LoginRequest;
import com.app.authjwt.dto.payload.request.RegisterRequest;
import com.app.authjwt.dto.payload.response.AuthResponse;
import com.app.authjwt.dto.payload.response.TokenValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/v1/rest")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para login y registro de usuarios")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario y retorna un token JWT válido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PostMapping(value = "signin")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario. Verifica si el email o username ya existen."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "El email o username ya están en uso", content = @Content)
    })
    @PostMapping(value = "signup")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Validar Token JWT",
            description = "Verifica si el token enviado en el Header es válido. Si el token es inválido o ha expirado, el filtro de seguridad retornará 401/403 antes de llegar aquí.",
            security = @SecurityRequirement(name = "bearerAuth") // Importante para Swagger
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenValidationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado (Manejado por Security)", content = @Content)
    })
    @GetMapping(value = "validate")
    public ResponseEntity<TokenValidationResponse> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(TokenValidationResponse.builder()
                .valid(true)
                .username(authentication.getName())
                .message("El token es válido y está activo.")
                .build());
    }

}

