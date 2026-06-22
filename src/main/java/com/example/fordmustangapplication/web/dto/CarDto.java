package com.example.fordmustangapplication.web.dto;

import com.example.fordmustangapplication.user.model.User;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

@Getter
@Setter
public class CarDto {

    private UUID id;

    @NotBlank(message = "Model is required!")
    private String model;

    @NotBlank(message = "Year is required")
    @Pattern(regexp = "\\d{4}", message = "Year must be 4 digits")
    private String year;

    @NotBlank(message = "Engine is required!")
    private String engine;


    @NotBlank(message = "Image URL is required")
    @Size(max = 150, message = "Image URL must be at most 150 characters")
    @URL(message = "Invalid URL format")
    private String imageUrl;

    @NotNull(message = "Price is required!")
    @Positive(message = "Price must be positive!")
    private Double price;

    private User owner;

    private UUID userId;

    private boolean visible;
}
