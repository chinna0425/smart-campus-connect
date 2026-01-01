package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyJpaRepo extends JpaRepository<Faculty,Long> {
    boolean existsByEmployeeId(String empId);

    List<Faculty> findByBranch(String branch);
}
