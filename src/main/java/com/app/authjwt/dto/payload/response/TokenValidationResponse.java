package com.app.authjwt.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private String message;
}