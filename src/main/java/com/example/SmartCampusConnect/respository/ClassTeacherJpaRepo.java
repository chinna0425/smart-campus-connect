package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.model.ClassTeacher;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassTeacherJpaRepo extends JpaRepository<ClassTeacher,Long> {
    @Query(value = "SELECT * FROM subject_teacher WHERE (:branch IS NULL OR branch = :branch) AND (:section IS NULL OR section = :section) AND (:year IS NULL OR year = :year)", nativeQuery = true)
    List<ClassTeacher> findByFilters(
            @Param("branch") String branch,
            @Param("section") String section,
            @Param("year") Integer year
    );

    ClassTeacher findByBranchAndSectionAndYear(String branch, String section, Integer year);

    boolean existsByFaculty_BranchAndSectionAndYear(String branch,String section,Integer year);

    boolean existsByFaculty_IdAndBranchAndSectionAndYear(Long FacultyId,String branch, String section, Integer year);

    ClassTeacher findByFaculty_Id(Long userId);

    List<ClassTeacher> findByBranch(String branch);
}
