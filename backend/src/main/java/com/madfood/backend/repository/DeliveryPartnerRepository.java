package com.madfood.backend.repository;

import com.madfood.backend.entity.DeliveryPartner;
import com.madfood.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    Optional<DeliveryPartner> findByUser(User user);
    Optional<DeliveryPartner> findByUserEmail(String email);
    List<DeliveryPartner> findByStatus(String status);
}
