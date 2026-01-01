package com.example.SmartCampusConnect.controller;

import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherAssignmentRequest;
import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherUpdateDto;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyActionRequest;
import com.example.SmartCampusConnect.dtos.studentDto.StudentUpdateDto;
import com.example.SmartCampusConnect.model.UserDetailsImpl;
import com.example.SmartCampusConnect.service.ClassTeacherService;
import com.example.SmartCampusConnect.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClassTeacherController {

    @Autowired
    private ClassTeacherService classTeacherService;

    @Autowired
    private StudentService studentService;

    @PostMapping("/class-teacher/assign")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> assignClassTeacher(@Valid @RequestBody ClassTeacherAssignmentRequest classTeacherReq) {
        Long hodId = currentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(classTeacherService.assignClassTeacher(hodId,classTeacherReq));
    }

    // 3 Updating the Class Teacher
    @PutMapping("/class-teacher/update/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> updateClassTeacher(
            @PathVariable Long id,
            @Valid @RequestBody ClassTeacherUpdateDto dto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(classTeacherService.updateClassTeacher(id, dto));
    }

    //  Get Class Teachers By dynamic filtering
    @GetMapping("/class-teachers")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> getClassTeachers(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) Integer year) {
        Long hodId = currentUserId();

        return ResponseEntity.ok(classTeacherService.filterClassTeachers(hodId,branch, section, year));
    }

    @GetMapping("/class-teacher/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> getClassTeacherById(@PathVariable Long id){
        Long hodId = currentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(classTeacherService.getClassTeacherById(hodId,id));
    }


    // Get all pending requests assigned to Class Teacher
    @GetMapping("/class-teacher/requests")
    @PreAuthorize("hasAnyRole('FACULTY')")
    public ResponseEntity<?> getAssignedRequests() {
        Long facultyId = currentUserId();   // from JWT
        return ResponseEntity.ok(classTeacherService.getAssignedRequests(facultyId));
    }

    // Approve/Reject a request
    @PostMapping("/class-teacher/request/action")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> handleRequestAction(@RequestBody @Valid FacultyActionRequest dto) {
        Long facultyId = currentUserId();
        return ResponseEntity.ok(classTeacherService.handleRequestAction(facultyId, dto));
    }

    @GetMapping("/student/my-class-teacher")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getClassTeacherForStudent() {
        Long studentId = currentUserId();  // from JWT
        return ResponseEntity.ok(classTeacherService.getClassTeacherForStudent(studentId));
    }

    @DeleteMapping("/delete/class-teacher/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> deleteClassTeacherById(@PathVariable Long id){
        Long hodId = currentUserId();
        classTeacherService.deleteClassTeacherById(hodId,id);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUserId();
        }
        throw new RuntimeException("User authentication failed");
    }

}
