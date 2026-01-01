package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.model.Student;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentJpaRepo extends JpaRepository<Student,Long> {
    Optional<Student> findByStudentId(Long studentId);

    boolean existsByRollNumber(@NotBlank(message = "Roll number is required") String rollNumber);

    Optional<Student>findByRollNumber(String rollNumber);
}
