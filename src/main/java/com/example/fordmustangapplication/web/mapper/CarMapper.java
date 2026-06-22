package com.example.fordmustangapplication.web.mapper;

import com.example.fordmustangapplication.web.dto.CarDto;
import com.example.fordmustangapplication.car.model.Car;
import com.example.fordmustangapplication.user.model.User;

import java.time.*;

public class CarMapper {

    public static Car toCarMapperEntity(CarDto dto, User owner) {
        Car car = new Car();
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setEngine(dto.getEngine());
        car.setPrice(dto.getPrice());
        car.setOwner(owner);
        car.setImageUrl(dto.getImageUrl());
        car.setCreatedAt(LocalDateTime.now());
        return car;
    }


    public static CarDto toCarMapperDto(Car car) {
        CarDto carDto = new CarDto();
        carDto.setId(car.getId());
        carDto.setModel(car.getModel());
        carDto.setYear(car.getYear());
        carDto.setEngine(car.getEngine());
        carDto.setPrice(car.getPrice());
        carDto.setImageUrl(car.getImageUrl());

        if (car.getOwner() != null) {
            carDto.setOwner(car.getOwner());
        }
        return carDto;
    }
}
