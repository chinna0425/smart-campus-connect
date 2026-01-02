package com.example.SmartCampusConnect.controller;

import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherAssignmentRequest;
import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherUpdateDto;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyUpdateRequest;
import com.example.SmartCampusConnect.dtos.hodDto.HodActionRequest;
import com.example.SmartCampusConnect.dtos.hodDto.HodUpdateReq;
import com.example.SmartCampusConnect.dtos.studentDto.StudentUpdateDto;
import com.example.SmartCampusConnect.dtos.subjectTeacherDto.AssignSubjectTeacherDto;
import com.example.SmartCampusConnect.dtos.subjectTeacherDto.SubjectTeacherUpdateRequest;
import com.example.SmartCampusConnect.model.UserDetailsImpl;
import com.example.SmartCampusConnect.service.ClassTeacherService;
import com.example.SmartCampusConnect.service.FacultyService;
import com.example.SmartCampusConnect.service.HodService;
import com.example.SmartCampusConnect.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/hod")
public class HodController {

    @Autowired
    private HodService hodService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ClassTeacherService classTeacherService;

    @Autowired
    private FacultyService facultyService;

    //  Getting all Hods
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allHods")
    public ResponseEntity<?> getAllHods(){
        return ResponseEntity.status(HttpStatus.OK).body(hodService.getAllHods());
    }

    // Getting the Profile
    @GetMapping("/profile")
    public ResponseEntity<?> getHodProfile(){
        Long hodId = currentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(hodService.getHodProfile(hodId));
    }

    // Getting the Hod By Id
    @GetMapping("/{id}")
    public ResponseEntity<?> getHodById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(hodService.getHodById(id));
    }

    @GetMapping("/student/my-hod")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getHodForStudent() {
        Long studentId = currentUserId();  // from JWT
        return ResponseEntity.ok(hodService.getHodForStudent(studentId));
    }

    // Get all requests that faculty approved for HOD review
    @GetMapping("/requests")
    public ResponseEntity<?> getRequestsForHod() {
        Long hodId = currentUserId();
        return ResponseEntity.ok(hodService.getPendingRequests(hodId));
    }

    @PreAuthorize("hasRole('HOD')")
    @PostMapping("/request/action")
    public ResponseEntity<?> hodAction(@RequestBody HodActionRequest dto) {
        Long hodId = currentUserId();
        return ResponseEntity.ok(hodService.handleHodAction(hodId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update-hod/{id}")
    public ResponseEntity<?> updateHodDetails(@PathVariable Long id, @RequestBody HodUpdateReq req){
        return ResponseEntity.ok(hodService.updateHodDetails(req,id));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-hod/{id}")
    public ResponseEntity<?> deleteHod(@PathVariable Long id) {
        Long currentId=currentUserId();
        hodService.deleteHod(currentId,id);
        return ResponseEntity.ok("HOD deleted successfully");
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
