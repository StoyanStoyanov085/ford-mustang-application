package com.example.fordmustangapplication.web;

import com.example.fordmustangapplication.exception.UserNotFoundException;
import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.web.dto.EditRequest;
import com.example.fordmustangapplication.web.dto.LoginRequest;
import com.example.fordmustangapplication.web.dto.RegisterRequest;
import com.example.fordmustangapplication.user.service.UserService;
import com.example.fordmustangapplication.web.dto.UserDto;
import com.example.fordmustangapplication.web.mapper.DtoMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;


@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        LoginRequest loginRequest = LoginRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", loginRequest);

        return modelAndView;
    }


    @PostMapping("/login")
    public ModelAndView login(@Valid LoginRequest loginRequest,
                              BindingResult bindingResult,
                              HttpSession httpSession) {

        ModelAndView modelAndView = new ModelAndView("login");

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }

        try {
            UserDto user = userService.login(loginRequest);

            if (user.isBanned()) {
                modelAndView.addObject("blocked", true);
                return modelAndView;
            }

            httpSession.setAttribute("user_id", user.getId());
            return new ModelAndView("redirect:/home");

        } catch (UserNotFoundException e) {
            modelAndView.addObject("errorMessage", e.getMessage());
            return modelAndView;
        }
    }


    @GetMapping("/register")
    public ModelAndView register() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("userRegisterDto", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(
            @ModelAttribute("userRegisterDto") @Valid RegisterRequest registerRequest,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        boolean hasSuccessfulRegistration = userService.register(registerRequest);

        if (!hasSuccessfulRegistration) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("usernameAlreadyExistMessage", "The user already exists!");
            return modelAndView;
        }

        redirectAttributes.addFlashAttribute("successfulRegistration", "You have registered successfully");
        return new ModelAndView("redirect:/login");

    }


    @GetMapping("/{id}/profile")
    public ModelAndView getProfileMenu(@PathVariable UUID id,
                                       HttpSession session) {

        User user = userService.getById(id);

        UUID currentUserId = (UUID) session.getAttribute("user_id");
        User currentUser = userService.getById(currentUserId);

        EditRequest editRequest = DtoMapper.mapUserToUserEditRequest(user);

        editRequest.setUsername(user.getUsername());
        editRequest.setRole(user.getRole());

        ModelAndView modelAndView = new ModelAndView("edit-profile");

        modelAndView.addObject("users", userService.getAllUsers());
        modelAndView.addObject("user", user);
        modelAndView.addObject("currentUser", currentUser);
        modelAndView.addObject("userEditRequest", editRequest);

        return modelAndView;
    }


    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(
            @PathVariable UUID id,
            @Valid @ModelAttribute("userEditRequest") EditRequest editRequest,
            BindingResult bindingResult,
            HttpSession session) {

        if (editRequest.getRole() == null) {
            User userFromDb = userService.getById(id);
            editRequest.setRole(userFromDb.getRole());
        }

        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);

            UUID currentUserId = (UUID) session.getAttribute("user_id");
            User currentUser = userService.getById(currentUserId);

            ModelAndView modelAndView = new ModelAndView("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("userEditRequest", editRequest);

            return modelAndView;
        }

        UUID currentUserId = (UUID) session.getAttribute("user_id");
        userService.editUserDetails(id, editRequest, currentUserId);
        return new ModelAndView("redirect:/home");
    }


    @GetMapping("/{id}/view")
    public ModelAndView viewUserProfile(@PathVariable UUID id) {
        User user = userService.getById(id);
        EditRequest editRequest = DtoMapper.mapUserToUserEditRequest(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-view");
        modelAndView.addObject("user", editRequest);

        return modelAndView;
    }


    @GetMapping("/logout")
    public String getLogoutPage(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
