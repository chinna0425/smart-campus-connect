package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.model.Hod;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HodJpaRepo extends JpaRepository<Hod,Long> {
    boolean existsByBranch(String branch);

    boolean existsByHodId(String hodId);

    Hod findByBranch(String branch);
}
