package com.example.fordmustangapplication.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {


    @Size(min = 3, max = 20, message = "Username length must be between 3 and 20 characters!")
    private String username;


    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String password;

    @Email(message = "Please enter a valid email!")
    @NotBlank(message = "Email address cannot be empty!")
    private String email;

    private String confirmPassword;

    private String role;

}
