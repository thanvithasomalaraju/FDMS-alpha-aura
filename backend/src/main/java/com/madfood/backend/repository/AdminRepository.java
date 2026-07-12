package com.madfood.backend.repository;

import com.madfood.backend.entity.Admin;
import com.madfood.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUser(User user);
    Optional<Admin> findByUserEmail(String email);
}
