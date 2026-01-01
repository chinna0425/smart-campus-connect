package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminJpaRepo extends JpaRepository<Admin,Long> {
    boolean existsByAdminId(String adminId);
}
