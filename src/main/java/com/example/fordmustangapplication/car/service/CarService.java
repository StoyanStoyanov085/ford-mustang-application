package com.example.fordmustangapplication.car.service;

import com.example.fordmustangapplication.bought_car.model.BoughtCar;
import com.example.fordmustangapplication.bought_car.service.BoughtCarService;
import com.example.fordmustangapplication.exception.CarNotFoundException;
import com.example.fordmustangapplication.web.dto.CarDto;
import com.example.fordmustangapplication.car.model.Car;
import com.example.fordmustangapplication.web.dto.CarWithBuyer;
import com.example.fordmustangapplication.web.dto.HomePageDto;
import com.example.fordmustangapplication.web.mapper.CarMapper;
import com.example.fordmustangapplication.user.service.UserService;
import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.car.repository.CarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;


@Service
public class CarService {

    private final CarRepository carRepository;
    private final UserService userService;
    private final BoughtCarService boughtCarService;


    @Autowired
    public CarService(CarRepository carRepository, @Lazy UserService userService, BoughtCarService boughtCarService) {
        this.carRepository = carRepository;
        this.userService = userService;
        this.boughtCarService = boughtCarService;
    }


    public boolean createCar(CarDto carDto) {

        if (carDto.getUserId() == null) {
            throw new IllegalArgumentException("UserId not null!");
        }

        User owner = userService.getById(carDto.getUserId());

        Car car = CarMapper.toCarMapperEntity(carDto, owner);
        carRepository.save(car);
        return true;
    }

    public CarDto updateFromMyCars(UUID id, CarDto carDto) {

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException("Car not found: " + id));

        if (carDto.getModel() != null && carDto.getYear() != null && carDto.getEngine() != null
                && carDto.getPrice() != null && carDto.getImageUrl() != null) {

            car.setModel(carDto.getModel());
            car.setYear(carDto.getYear());

            car.setEngine(carDto.getEngine());
            car.setPrice(carDto.getPrice());
            car.setImageUrl(carDto.getImageUrl());
        }

        if (carDto.getOwner() != null) {
            User owner = userService.getById(carDto.getOwner().getId());
            car.setOwner(owner);
        }
        Car saved = carRepository.save(car);
        return CarMapper.toCarMapperDto(saved);
    }


    public List<Car> listMyCars(String username) {
        User user = userService.getByUsername(username).orElse(null);
        return carRepository.findAllByOwnerAndVisibleTrue(user);
    }


    public Car getById(UUID id) {
        return carRepository.findById(id).orElseThrow(() -> new CarNotFoundException("Car with [%s] id does not exist.".formatted(id)));
    }


    @Transactional
    public void removeFromMyCars(UUID id, String username) {
        User user = userService.getByUsername(username).orElse(null);
        if (user == null) {
            return;
        }

        Car car = user.getCars().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (car == null) {
            return;
        }

        if (boughtCarService.isCarBought(car)) {
            return;
        }
        user.getCars().remove(car);
        carRepository.deleteById(id);
    }

    public CarDto findById(UUID id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException("Car not found: " + id));
        return CarMapper.toCarMapperDto(car);
    }


    public List<Car> getAllCars() {
        return carRepository.findAll();
    }


    public Optional<Car> findEntityById(UUID id) {
        return carRepository.findById(id);
    }


    public void save(Car car) {
        carRepository.save(car);
    }


    @Transactional
    public void deleteBoughtCarById(UUID carId, UUID userId) {

        User user = userService.findById(userId);
        if (user == null) {
            return;
        }

        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isEmpty()) {
            return;
        }

        Car car = carOpt.get();
        boughtCarService.deleteBoughtCarByUserAndCar(user, car);
    }


    @Transactional
    public void incrementVotesByOne(UUID carId, User currentUser) {

        User managedUser = userService.getById(currentUser.getId());
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found!"));

        if (car.getOwner().getId().equals(managedUser.getId())) {
            return;
        }

        if (car.getVotes().contains(managedUser)) {
            return;
        }

        car.setVote(car.getVote() + 1);
        carRepository.save(car);
        car.getVotes().add(managedUser);
        managedUser.getVotedCars().add(car);
        userService.save(managedUser);
    }


    public List<Car> getAllCarsWithVotesExcludingOwner(String username) {
        return carRepository.findAllWithVotesExcludingOwner(username);
    }


    public void switchStatus(UUID carId) {

        Car car = getById(carId);

        car.setActive(!car.isActive());
        carRepository.save(car);
    }


    public HomePageDto getHomePageCars(User user) {
        HomePageDto data = new HomePageDto();


        List<Car> myCarList = listMyCars(user.getUsername());
        data.setMyCars(myCarList);


        List<Car> otherCarList = getAllCars()
                .stream()
                .filter(car -> !car.getOwner().getUsername().equals(user.getUsername()))
                .filter(car -> !car.isSold())
                .filter(Car::isActive)
                .collect(Collectors.toList());
        data.setOtherCars(otherCarList);


        List<BoughtCar> boughtCars = boughtCarService.findByUser(user);

        List<CarWithBuyer> carWithBuyers = boughtCars.stream()
                .map(bc -> new CarWithBuyer(bc.getCar(), bc.getUser(), bc.getDate()))
                .collect(Collectors.toList());
        data.setMyBoughtCars(carWithBuyers);


        List<Car> mostRatedCarList = getAllCarsWithVotesExcludingOwner(user.getUsername())
                .stream()
                .filter(car -> !car.getOwner().getUsername().equals(user.getUsername()))
                .filter(car -> car.getVotes() != null && !car.getVotes().isEmpty())
                .collect(Collectors.toList());
        data.setMostRatedCars(mostRatedCarList);

        return data;
    }
}



