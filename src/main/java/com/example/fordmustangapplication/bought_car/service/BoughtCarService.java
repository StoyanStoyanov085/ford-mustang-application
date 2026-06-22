package com.example.fordmustangapplication.bought_car.service;

import com.example.fordmustangapplication.bought_car.model.BoughtCar;
import com.example.fordmustangapplication.bought_car.repository.BoughtCarRepository;
import com.example.fordmustangapplication.car.model.Car;
import com.example.fordmustangapplication.car.service.CarService;
import com.example.fordmustangapplication.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class BoughtCarService {

    private final BoughtCarRepository boughtCarRepository;
    private final CarService carService;

    @Autowired
    public BoughtCarService(BoughtCarRepository boughtCarRepository, @Lazy CarService carService) {
        this.boughtCarRepository = boughtCarRepository;
        this.carService = carService;
    }

    public List<BoughtCar> findByUser(User user) {
        return boughtCarRepository.findByUser(user);
    }

    public boolean isCarBought(Car car) {
        return boughtCarRepository.existsByCar(car);
    }

    public boolean isCarAlreadyBoughtByUser(User user, Car car) {
        return boughtCarRepository.existsByUserAndCar(user, car);
    }

    @Transactional
    public void deleteBoughtCarByUserAndCar(User user, Car car) {

        Optional<BoughtCar> boughtCarOpt = boughtCarRepository.findByUserAndCar(user, car);
        boughtCarOpt.ifPresent(boughtCarRepository::delete);
    }

    public void buyCar(User user, Car car) {

        BoughtCar boughtCar = BoughtCar.builder()
                .date(LocalDate.now())
                .user(user)
                .car(car)
                .build();

        boughtCarRepository.save(boughtCar);
        car.setSold(true);
        car.setVisible(false);
        car.setActive(false);
        car.setDeleted(true);
        carService.save(car);
    }
}
