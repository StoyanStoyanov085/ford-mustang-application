package com.example.fordmustangapplication.user.repository;

import com.example.fordmustangapplication.user.model.User;
import com.example.fordmustangapplication.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsernameOrEmail(String username, String email);

    User findByUsername(String username);

    Optional<User> findUserByUsername(String username);

    Optional<User> findOptionalByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cars WHERE u.username = :username")
    Optional<User> findByUsernameWithCars(@Param("username") String username);

    Optional<User> findByEmail(String email);

    long countByRole(UserRole role);

}
