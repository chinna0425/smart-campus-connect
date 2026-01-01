package com.example.SmartCampusConnect.controller;

import com.example.SmartCampusConnect.dtos.AdminDto.AdminCreateDto;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyRegistrationRequest;
import com.example.SmartCampusConnect.dtos.hodDto.HodRegistrationRequest;
import com.example.SmartCampusConnect.dtos.loginDto.LoginRequest;
import com.example.SmartCampusConnect.dtos.studentDto.StudentRegistrationRequest;
import com.example.SmartCampusConnect.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register-admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminCreateDto adminCreateReq){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerAdmin(adminCreateReq));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/student/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody StudentRegistrationRequest studentReq){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerStudent(studentReq));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/faculty/register")
    public ResponseEntity<?> registerFaculty(@Valid @RequestBody FacultyRegistrationRequest facultyReq){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerFaculty(facultyReq));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hod/register")
    public ResponseEntity<?> registerHod(@Valid @RequestBody HodRegistrationRequest hodReq){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerHod(hodReq));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        String token = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest loginRequest){
        String token = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token));
    }
}
