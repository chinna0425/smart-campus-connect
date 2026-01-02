package com.example.SmartCampusConnect.controller;
import com.example.SmartCampusConnect.dtos.request.CreateRequest;
import com.example.SmartCampusConnect.dtos.studentDto.StudentUpdateDto;
import com.example.SmartCampusConnect.model.UserDetailsImpl;
import com.example.SmartCampusConnect.service.ClassTeacherService;
import com.example.SmartCampusConnect.service.StudentService;
import com.example.SmartCampusConnect.service.SubjectTeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ClassTeacherService classTeacherService;

    @Autowired
    private SubjectTeacherService subjectTeacherService;

    @GetMapping("/student/getAllStudents")
    @PreAuthorize("hasAnyRole('FACULTY','HOD','ADMIN')")
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/student/{id}")
    @PreAuthorize("hasAnyRole('FACULTY','HOD','ADMIN')") // FIXED
    public ResponseEntity<?> getStudentById(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/student/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentProfile() {
        Long studentId = currentUserId();
        return ResponseEntity.ok(studentService.getStudentProfile(studentId));
    }

    @PostMapping("/student/request")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createRequest(@RequestBody @Valid CreateRequest request) {
        Long studentId = currentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.createRequest(studentId, request));
    }


    @PutMapping("/student/update/{id}")
    @PreAuthorize("hasAnyRole('FACULTY','HOD','ADMIN')")
    public ResponseEntity<?> updateStudent(@PathVariable Long id,
                                           @RequestBody StudentUpdateDto dto) throws AccessDeniedException {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    @GetMapping("/student/requests")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyRequests() {
        Long studentId = currentUserId();
        return ResponseEntity.ok(studentService.getStudentRequests(studentId));
    }

    @GetMapping("/student/request/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','FACULTY','HOD')")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(studentService.getRequestById(id)); // RBAC happens in service
    }

    @GetMapping("/student/{id}/requests")
    @PreAuthorize("hasAnyRole('FACULTY','HOD')")
    public ResponseEntity<?> getStudentRequestsForTeacher(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(studentService.getRequestsForTeacher(id));
    }

    @PreAuthorize("hasRoleAny('ADMIN''HOD')")
    @DeleteMapping("/delete-student/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        Long currentId=currentUserId();
        studentService.deleteStudent(currentId,id);
        return ResponseEntity.ok("Student deleted successfully");
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
