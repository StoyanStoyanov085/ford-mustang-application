package com.example.fordmustangapplication.user.service;

import com.example.fordmustangapplication.car.model.Car;
import com.example.fordmustangapplication.car.service.CarService;
import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.user.property.UserProperties;
import com.example.fordmustangapplication.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class UserInit implements ApplicationRunner {

    private final UserService userService;
    private final UserProperties userProperties;
    private final CarService carService;

    @Autowired
    public UserInit(UserService userService, UserProperties userProperties, CarService carService) {
        this.userService = userService;
        this.userProperties = userProperties;
        this.carService = carService;
    }

    @Override
    public void run(ApplicationArguments args) {

        List<User> users = userService.getAllUsers();

        if (users.stream().noneMatch(user -> user.getUsername().equals(userProperties.getDefaultUser().getUsername()))) {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username(userProperties.getDefaultUser().getUsername())
                    .password(userProperties.getDefaultUser().getPassword())
                    .email(userProperties.getDefaultUser().getEmail())
                    .role(userProperties.getDefaultUser().getRole().name())
                    .confirmPassword(userProperties.getDefaultUser().getPassword())
                    .build();
            userService.register(registerRequest);
        }

        if (users.stream().noneMatch(user -> user.getUsername().equals(userProperties.getSecond().getUsername()))) {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username(userProperties.getSecond().getUsername())
                    .password(userProperties.getSecond().getPassword())
                    .email(userProperties.getSecond().getEmail())
                    .confirmPassword(userProperties.getSecond().getPassword())
                    .build();
            userService.register(registerRequest);
        }

        userService.getByUsernameWithCars(userProperties.getDefaultUser().getUsername())
                .ifPresent(user -> addCarsToUserIfNone(user, userProperties.getDefaultUser().getCars()));

        userService.getByUsernameWithCars(userProperties.getSecond().getUsername())
                .ifPresent(user -> addCarsToUserIfNone(user, userProperties.getSecond().getCars()));
    }

    private void addCarsToUserIfNone(User user, List<UserProperties.CarConfig> carConfigs) {
        if (user != null && (user.getCars() == null || user.getCars().isEmpty())) {
            for (UserProperties.CarConfig carConfig : carConfigs) {
                Car car = new Car();
                car.setModel(carConfig.getModel());
                car.setYear(carConfig.getYear());
                car.setEngine(carConfig.getEngine());
                car.setImageUrl(carConfig.getImageUrl());
                car.setPrice(carConfig.getPrice());
                car.setOwner(user);

                carService.save(car);
            }
        }
    }
}



