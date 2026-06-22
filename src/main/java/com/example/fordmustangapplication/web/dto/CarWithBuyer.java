package com.example.fordmustangapplication.web.dto;

import com.example.fordmustangapplication.car.model.Car;
import com.example.fordmustangapplication.user.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CarWithBuyer {

    private Car car;
    private User buyer;
    private LocalDate date;

    public CarWithBuyer(Car car, User user, LocalDate date) {
        this.car = car;
        this.buyer = user;
        this.date = date;
    }

    public String getFormattedDate() {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "No purchase date";
    }

    @Override
    public String toString() {
        return car + " | " + buyer + " | " + getFormattedDate();
    }

}
