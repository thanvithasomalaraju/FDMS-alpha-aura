package com.madfood.fdms.repository;

import com.madfood.fdms.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> { }
