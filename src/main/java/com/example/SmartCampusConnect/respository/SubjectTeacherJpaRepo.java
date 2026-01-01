package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.model.SubjectTeacher;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectTeacherJpaRepo extends JpaRepository<SubjectTeacher,Long> {
    boolean existsByFaculty_Id(Long facultyId);

    @Query(value = "SELECT * FROM subject_teacher WHERE (:branch IS NULL OR branch = :branch) AND (:section IS NULL OR section = :section) AND (:year IS NULL OR year = :year)",nativeQuery = true)
    List<SubjectTeacher> findByFilters(
            @Param("branch") String branch,
            @Param("section") String section,
            @Param("year") Integer year);

    List<SubjectTeacher> findByBranchAndSectionAndYear(String branch, String section, Integer year);
    boolean existsBySubjectNameAndBranchAndSectionAndYear(String subjectName,String branch, String section,Integer year);

    boolean existsByFaculty_IdAndBranchAndSectionAndYear(Long userId, String branch, String branch1, Integer year);

    SubjectTeacher findByFaculty_Id(Long currentId);
}
