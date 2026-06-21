package com.example.fordmustangapplication.web.dto;

import com.example.fordmustangapplication.user.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

@Data
@Builder
public class EditRequest {

    private UUID id;

    @Size(max = 8, message = "Username must be up to 8 characters!")
    private String username;

    @Size(max = 8, message = "First name must be up to 8 characters!")
    private String firstName;

    @Size(max = 8, message = "Last name must be up to 8 characters!")
    private String lastName;

    @Email(message = "Invalid email format!")
    @Size(min = 12, max = 30, message = "Email must include @ and text after it!")
    private String email;

    @URL(message = "Wrong URL profilePicture!")
    private String profilePicture;

    private UserRole role;
}
