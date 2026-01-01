package com.example.SmartCampusConnect.controller;

import com.example.SmartCampusConnect.dtos.facultyDto.FacultyActionRequest;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyUpdateRequest;
import com.example.SmartCampusConnect.model.UserDetailsImpl;
import com.example.SmartCampusConnect.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @GetMapping("/faculty/getAllFaculties")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> getAllFaculty(){
        Long currentId=currentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(facultyService.getAllFacultyDetails(currentId));
    }

    @GetMapping("/faculty/profile")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getFacultyProfile(){
        Long facultyId = currentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(facultyService.getFacultyProfile(facultyId));
    }

    @GetMapping("/faculty/{id}")
    @PreAuthorize("hasAnyRole('HOD')")
    public ResponseEntity<?> getFacultyById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(facultyService.getFacultyById(id));
    }

    @PutMapping("/faculty/update/{id}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<?> updateFacultyById(@PathVariable Long id,@RequestBody FacultyUpdateRequest facultyUpdateRequest){
        Long hodId=currentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(facultyService.updateFacultyById(hodId,id,facultyUpdateRequest));
    }

    @PreAuthorize("hasRole('ADMIN','HOD')")
    @DeleteMapping("/delete-faculty/{id}")
    public ResponseEntity<?> deleteFaculty(@PathVariable Long id) {
        Long currentId=currentUserId();
        facultyService.deleteFaculty(currentId,id);
        return ResponseEntity.ok("Faculty deleted successfully");
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
