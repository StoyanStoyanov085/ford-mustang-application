package com.example.fordmustangapplication.web;

import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.user.model.UserRole;
import com.example.fordmustangapplication.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    public ModelAndView viewAllUsers(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");

        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.findById(userId);

        if (user.getRole() != UserRole.ADMIN) {
            return new ModelAndView("redirect:/home");
        }

        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView("admin-users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }


    @PostMapping("/ban/{id}")
    public String banUser(@PathVariable UUID id) {
        User user = userService.getById(id);
        if (user != null && user.getRole() != UserRole.ADMIN) {
            user.setBanned(true);
            user.setActive(false);
            userService.save(user);
        }
        return "redirect:/admin/users";
    }


    @PostMapping("/unban/{id}")
    public String unbanUser(@PathVariable UUID id) {
        User user = userService.getById(id);
        if (user != null && user.getRole() != UserRole.ADMIN) {
            user.setBanned(false);
            user.setActive(true);
            userService.save(user);
        }
        return "redirect:/admin/users";
    }
}
