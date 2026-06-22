package com.example.fordmustangapplication.web.dto;

import com.example.fordmustangapplication.car.model.Car;
import lombok.Data;


import java.util.List;

@Data
public class HomePageDto {

    private List<Car> myCars;
    private List<Car> otherCars;
    private List<CarWithBuyer> myBoughtCars;
    private List<Car> mostRatedCars;
}
