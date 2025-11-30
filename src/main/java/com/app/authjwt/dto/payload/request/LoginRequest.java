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
public class LoginRequest {

    @Schema(description = "Nombre de usuario", example = "juanperez")
    private String username;

    @Schema(description = "Contraseña del usuario", example = "123456")
    private String password;
}