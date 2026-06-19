package com.example.fordmustangapplication.user.property;

import com.example.fordmustangapplication.user.model.UserRole;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "users")
public class UserProperties {

    private DefaultUser defaultUser;
    private DefaultUser second;

    @Data
    public static class DefaultUser {

        private String username;

        private String password;

        private String email;

        private UserRole role;

        private String confirmPassword;

        private List<CarConfig> cars = new ArrayList<>();
    }


    @Data
    public static class CarConfig {

        private String model;

        private String year;

        private String engine;

        private String imageUrl;

        private Double price;
    }
}