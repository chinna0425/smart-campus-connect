package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.model.Student;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentJpaRepo extends JpaRepository<Student,Long> {
    Optional<Student> findByStudentId(Long studentId);

    boolean existsByRollNumber(@NotBlank(message = "Roll number is required") String rollNumber);

    Optional<Student>findByRollNumber(String rollNumber);

    @Query(value = "SELECT * FROM student WHERE (:rollNumber IS NULL OR roll_number = :rollNumber) AND (:branch IS NULL OR branch = :branch) AND (:section IS NULL OR section = :section) AND (:year IS NULL OR year = :year)", nativeQuery = true)
    List<Student> filterStudents(
            @Param("rollNumber") String rollNumber,
            @Param("branch") String branch,
            @Param("section") String section,
            @Param("year") Integer year
    );
}
