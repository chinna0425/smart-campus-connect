package com.example.SmartCampusConnect.controller;

import com.example.SmartCampusConnect.dtos.subjectTeacherDto.AssignSubjectTeacherDto;
import com.example.SmartCampusConnect.dtos.subjectTeacherDto.SubjectTeacherUpdateRequest;
import com.example.SmartCampusConnect.model.UserDetailsImpl;
import com.example.SmartCampusConnect.service.SubjectTeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SubjectTeacherController {

    @Autowired
    private SubjectTeacherService subjectTeacherService;

    // Assign Subject Teacher
    @PostMapping("/subject-teacher/assign")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> assignSubjectTeacher(@Valid @RequestBody AssignSubjectTeacherDto dto) {
        Long hodId = currentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectTeacherService.assignSubjectTeacher(hodId,dto));
    }

    @GetMapping("/subject-teachers")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> getSubjectTeachers(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) Integer year
    ) {
        Long hodId = currentUserId();
        return ResponseEntity.ok(subjectTeacherService.getSubjectTeachers(hodId,branch, section, year));
    }

    @GetMapping("/subject-teacher/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> getSubjectTeacherById(@PathVariable Long id){
        Long hodId = currentUserId();
        return ResponseEntity.ok(subjectTeacherService.getSubjectTeacherById(hodId,id));
    }

    // Updating the Subject Teacher
    @PutMapping("/subject-teacher/update/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> updateSubjectTeacher(@PathVariable Long id,@Valid @RequestBody SubjectTeacherUpdateRequest dto){
        Long hodId = currentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(subjectTeacherService.updateSubjectTeacher(hodId,id,dto));
    }

    @DeleteMapping("/delete/subject-teacher/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> deleteSubjectTeacherById(@PathVariable Long id){
        Long hodId=currentUserId();
        subjectTeacherService.deleteSubjectTeacherById(hodId,id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-subject-teachers")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMySubjectTeachers() {
        Long studentId = currentUserId(); // extract from JWT
        return ResponseEntity.ok(subjectTeacherService.getSubjectTeachersForStudent(studentId));
    }

    // Extract logged-in user's ID from JWT
    private Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUserId();
        }
        throw new RuntimeException("User authentication failed");
    }
}
