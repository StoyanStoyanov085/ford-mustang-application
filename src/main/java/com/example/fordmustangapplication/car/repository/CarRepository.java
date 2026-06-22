package com.example.fordmustangapplication.car.repository;

import com.example.fordmustangapplication.car.model.Car;
import com.example.fordmustangapplication.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    List<Car> findAllByOwnerAndVisibleTrue(User owner);

    void flush();

    @Query("SELECT c FROM Car c LEFT JOIN FETCH c.votes WHERE c.owner.username <> :username AND size(c.votes) > 0")
    List<Car> findAllWithVotesExcludingOwner(@Param("username") String username);

}
