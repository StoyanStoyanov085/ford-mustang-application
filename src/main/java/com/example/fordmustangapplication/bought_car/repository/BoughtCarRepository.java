package com.example.fordmustangapplication.bought_car.repository;

import com.example.fordmustangapplication.bought_car.model.BoughtCar;
import com.example.fordmustangapplication.car.model.Car;
import com.example.fordmustangapplication.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoughtCarRepository extends JpaRepository<BoughtCar, UUID> {

    List<BoughtCar> findByUser(User user);

    boolean existsByCar(Car car);

    boolean existsByUserAndCar(User user, Car car);

    Optional<BoughtCar> findByUserAndCar(User user, Car car);
}
