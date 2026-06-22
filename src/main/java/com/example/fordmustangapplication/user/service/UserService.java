package com.example.fordmustangapplication.user.service;

import com.example.fordmustangapplication.web.dto.EditRequest;
import com.example.fordmustangapplication.web.dto.LoginRequest;
import com.example.fordmustangapplication.web.dto.RegisterRequest;
import com.example.fordmustangapplication.exception.UserNotFoundException;
import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.user.model.UserRole;
import com.example.fordmustangapplication.user.repository.UserRepository;
import com.example.fordmustangapplication.web.dto.UserDto;
import com.example.fordmustangapplication.web.mapper.DtoMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto login(LoginRequest loginRequest) {

        Optional<User> optionalUser =
                userRepository.findUserByUsername(loginRequest.getUsername());

        if (optionalUser.isEmpty() ||
                !passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
            throw new UserNotFoundException("Invalid username or password");
        }

        return DtoMapper.mapUserToUserDto(optionalUser.get());
    }


    public boolean register(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return false;
        }

        boolean existsByUsernameOrEmail = userRepository.
                existsByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());

        if (existsByUsernameOrEmail) {
            return false;
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRole.USER);


        String roleString = registerRequest.getRole();

        if (roleString == null) {
            user.setRole(UserRole.USER);
        } else {
            try {
                user.setRole(UserRole.valueOf(registerRequest.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(UserRole.USER);
            }
        }

        userRepository.save(user);

        log.info("New user profile was registered in the system for user [%s].".formatted(registerRequest.getUsername()));

        return true;
    }


    public User getById(UUID ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findOptionalByUsername(username);
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getByUsernameWithCars(String username) {
        return userRepository.findByUsernameWithCars(username);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }


    public void editUserDetails(UUID userId,
                                EditRequest userEditRequest,
                                UUID currentUserId) {

        User user = getById(userId);
        User currentUser = getById(currentUserId);

        Optional<User> existingEmailUser =
                userRepository.findByEmail(userEditRequest.getEmail());

        String emailToSetFinal =
                (existingEmailUser.isEmpty()
                        || existingEmailUser.get().getId().equals(user.getId()))
                        ? userEditRequest.getEmail()
                        : user.getEmail();

        Optional<User> existingUsernameUser =
                Optional.ofNullable(
                        userRepository.findByUsername(
                                userEditRequest.getUsername()));

        String usernameToSetFinal =
                (existingUsernameUser.isEmpty()
                        || existingUsernameUser.get().getId().equals(user.getId()))
                        ? userEditRequest.getUsername()
                        : user.getUsername();

        user.setUsername(usernameToSetFinal);
        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(emailToSetFinal);
        user.setProfilePicture(userEditRequest.getProfilePicture());

        if (currentUser.getRole() == UserRole.ADMIN) {

            long adminCount = userRepository.countByRole(UserRole.ADMIN);

            if (adminCount == 1
                    && user.getRole() == UserRole.ADMIN
                    && userEditRequest.getRole() == UserRole.USER) {
                return;
            }

            user.setRole(userEditRequest.getRole());
        }

        userRepository.save(user);
    }


    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}


