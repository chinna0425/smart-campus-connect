package com.example.SmartCampusConnect.controller;

import com.example.SmartCampusConnect.model.UserDetailsImpl;
import com.example.SmartCampusConnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // GET all notifications for logged-in faculty
    @GetMapping
    @PreAuthorize("hasAnyRole('FACULTY','HOD')")
    public ResponseEntity<?> getMyNotifications() {
        Long facultyId = currentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getMyNotifications(facultyId));
    }

    // Mark notification as read
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('FACULTY')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Long facultyId = currentUserId();
        return ResponseEntity.ok(notificationService.markAsRead(facultyId, id));
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