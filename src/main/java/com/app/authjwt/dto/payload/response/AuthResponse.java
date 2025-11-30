package com.app.authjwt.dto.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Fecha de emisión", example = "2023-10-01T10:00:00.000+00:00")
    private Date issuedAt;

    @Schema(description = "Fecha de expiración", example = "2023-10-02T10:00:00.000+00:00")
    private Date expiration;
}