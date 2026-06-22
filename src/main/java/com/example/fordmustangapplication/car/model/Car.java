package com.example.fordmustangapplication.car.model;

import com.example.fordmustangapplication.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    @Length(max = 4)
    private String year;

    @Column(nullable = false)
    private String engine;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    @Length(max = 150)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "car_votes",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> votes = new HashSet<>();

    @Column(nullable = false)
    private int vote = 0;

    @Column(nullable = false)
    private boolean isSold = false;

    @Column(nullable = false)
    private boolean visible = true;

    @Column(nullable = false)
    private boolean active = true;

    private boolean deleted = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
