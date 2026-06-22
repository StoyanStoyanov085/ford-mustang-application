package com.example.fordmustangapplication.web;

import com.example.fordmustangapplication.car.service.CarService;
import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.user.service.UserService;
import com.example.fordmustangapplication.web.dto.HomePageDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;


@Controller
public class HomeController {

    private final UserService userService;
    private final CarService carService;

    @Autowired
    public HomeController(UserService userService, CarService carService) {
        this.userService = userService;
        this.carService = carService;
    }


    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("/info-application")
    public String info() {
        return "info-application";
    }


    @GetMapping("/home")
    public ModelAndView home(HttpSession httpSession) {

        User user = userService.getById((UUID) httpSession.getAttribute("user_id"));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);

        HomePageDto homePageDto = carService.getHomePageCars(user);

        modelAndView.addObject("myCars", homePageDto.getMyCars());
        modelAndView.addObject("otherCar", homePageDto.getOtherCars());
        modelAndView.addObject("myBoughtCars", homePageDto.getMyBoughtCars());
        modelAndView.addObject("mostRatedCar", homePageDto.getMostRatedCars());

        return modelAndView;
    }
}

