package com.madfood.backend.repository;

import com.madfood.backend.entity.Restaurant;
import com.madfood.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByUser(User user);
    Optional<Restaurant> findByUserEmail(String email);
    List<Restaurant> findByStatus(String status);
}
