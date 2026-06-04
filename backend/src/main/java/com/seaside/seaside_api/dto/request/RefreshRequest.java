package com.seaside.seaside_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {
    
    @NotBlank(message =  "Le refresh token est obligatoire")
    private String refreshToken; 
}
