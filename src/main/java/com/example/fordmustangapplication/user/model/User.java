package com.example.fordmustangapplication.user.model;

import com.example.fordmustangapplication.car.model.Car;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Length(min = 3, max = 20)
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean banned = false;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Car> cars;

    @ManyToMany(mappedBy = "votes", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Car> votedCars = new HashSet<>();


}
