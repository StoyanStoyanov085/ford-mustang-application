package com.example.fordmustangapplication.web.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Size(min = 3, max = 20, message = "Username length must be between 3 and 20 characters!")
    private String username;

    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String password;

}
