package com.example.fordmustangapplication.web;

import com.example.fordmustangapplication.car.model.Car;
import com.example.fordmustangapplication.car.service.CarService;
import com.example.fordmustangapplication.exception.CarNotFoundException;
import com.example.fordmustangapplication.bought_car.service.BoughtCarService;
import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.user.service.UserService;
import com.example.fordmustangapplication.web.dto.CarDto;
import com.example.fordmustangapplication.web.mapper.CarMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class CarController {

    private final CarService carService;
    private final UserService userService;
    private final BoughtCarService boughtCarService;

    @Autowired
    public CarController(CarService carService, UserService userService, BoughtCarService boughtCarService) {
        this.carService = carService;
        this.userService = userService;
        this.boughtCarService = boughtCarService;
    }


    @GetMapping("/add-cars")
    public String addCar(HttpSession httpSession, Model model) {

        User user = userService.getById((UUID) httpSession.getAttribute("user_id"));

        model.addAttribute("user", user);

        if (!model.containsAttribute("carData")) {
            model.addAttribute("carData", new CarDto());
        }

        return "add-cars";
    }


    @PostMapping("/add-cars")
    public String doAddCar(HttpSession httpSession,
                           @ModelAttribute("carData") @Valid CarDto carDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        User user = userService.getById((UUID) httpSession.getAttribute("user_id"));
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/login";
        }
        carDto.setUserId(user.getId());


        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("carData", carDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.carData", bindingResult);
            return "redirect:/add-cars";
        }

        boolean success = carService.createCar(carDto);
        if (!success) {
            redirectAttributes.addFlashAttribute("carData", carDto);
            return "redirect:/add-cars";
        }
        return "redirect:/home";
    }


    @PostMapping("/cars/remove/{id}")
    public ModelAndView removeCar(HttpSession httpSession,
                                  @PathVariable("id") UUID id) {

        User user = userService.getById((UUID) httpSession.getAttribute("user_id"));
        carService.removeFromMyCars(id, user.getUsername());
        return new ModelAndView("redirect:/home");
    }


    @GetMapping("/cars/edit/{id}")
    public String showEditForm(HttpSession httpSession,
                               @PathVariable("id") UUID id, Model model) {

        CarDto carDto = carService.findById((UUID) httpSession.getAttribute("user_id"));
        if (carDto == null) {
            throw new CarNotFoundException("Car not found with id: " + id);
        }
        model.addAttribute("carData", carDto);
        return "car-edit";
    }


    @GetMapping("/cars/edits/{id}")
    public ModelAndView editCar(@PathVariable UUID id,
                                HttpSession httpSession) {

        Car car = carService.getById(id);
        UUID userId = (UUID) httpSession.getAttribute("user_id");

        if (!car.getOwner().getId().equals(userId)) {
            throw new CarNotFoundException("You are not allowed to edit this car.");
        }

        ModelAndView modelAndView = new ModelAndView("car-edit");
        modelAndView.addObject("carData", CarMapper.toCarMapperDto(car));
        return modelAndView;
    }


    @PostMapping("/cars/update/{id}")
    public ModelAndView updateCar(@PathVariable("id") UUID id,
                                  @ModelAttribute("carData") CarDto carDto,
                                  HttpSession httpSession) {


        UUID userId = (UUID) httpSession.getAttribute("user_id");

        Car car = carService.getById(id);
        if (!car.getOwner().getId().equals(userId)) {
            throw new CarNotFoundException("You are not allowed to update this car.");
        }

        CarDto updatedCar = carService.updateFromMyCars(id, carDto);

        if (updatedCar.getModel() == null) {
            throw new CarNotFoundException("Updated car model is null");
        }

        return new ModelAndView("redirect:/home");
    }


    @PostMapping("/buy/{id}")
    public ModelAndView buyCar(HttpSession httpSession,
                               @PathVariable("id") UUID carId) {

        User users = userService.getById((UUID) httpSession.getAttribute("user_id"));
        String username = users.getUsername();

        User user = userService.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Car car = carService.findEntityById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        if (boughtCarService.isCarAlreadyBoughtByUser(user, car)) {
            return new ModelAndView("redirect:/home");
        }

        boughtCarService.buyCar(user, car);
        return new ModelAndView("redirect:/home");
    }


    @DeleteMapping("cars/boughtCar/{id}")
    public String deleteBoughtCar(HttpSession httpSession, @PathVariable("id") UUID id) {

        UUID userId = (UUID) httpSession.getAttribute("user_id");

        if (userId == null) {
            return "redirect:/login";
        }

        carService.deleteBoughtCarById(id, userId);

        return "redirect:/home";
    }


    @PutMapping("cars/{id}/votes")
    public String updateVotes(HttpSession httpSession,
                              @PathVariable("id") UUID id) {

        User user = userService.getById((UUID) httpSession.getAttribute("user_id"));
        String username = user.getUsername();
        User currentUser = userService.findByUsername(username);
        carService.incrementVotesByOne(id, currentUser);
        return "redirect:/home";

    }


    @PatchMapping("/cars/{carId}/status")
    public String switchCarStatus(@PathVariable UUID carId) {

        carService.switchStatus(carId);
        return "redirect:/home";
    }
}
