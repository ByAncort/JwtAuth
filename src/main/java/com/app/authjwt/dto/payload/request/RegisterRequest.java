package com.app.authjwt.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Schema(description = "Nombre de usuario deseado", example = "juanperez")
    private String username;

    @Schema(description = "Correo electrónico válido", example = "juan@example.com")
    private String email;

    @Schema(description = "Contraseña segura", example = "password123")
    private String password;
}