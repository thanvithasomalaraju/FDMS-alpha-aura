package com.madfood.backend.repository;

import com.madfood.backend.model.DeliveryApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryApplicationRepository extends JpaRepository<DeliveryApplication, Long> {
}
