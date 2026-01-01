package com.example.SmartCampusConnect.service;

import com.example.SmartCampusConnect.dtos.NotificationDto.NotificationResponseDto;
import com.example.SmartCampusConnect.exception.ResourceNotFoundException;
import com.example.SmartCampusConnect.model.Faculty;
import com.example.SmartCampusConnect.model.Notification;
import com.example.SmartCampusConnect.respository.FacultyJpaRepo;
import com.example.SmartCampusConnect.respository.NotificationJpaRepo;
import com.example.SmartCampusConnect.respository.SubjectTeacherJpaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationJpaRepo notificationJpaRepo;

    @Autowired
    private FacultyJpaRepo facultyJpaRepo;

    @Autowired
    private SubjectTeacherJpaRepo subjectTeacherJpaRepo;

    // Get all notifications for logged-in faculty
    public List<NotificationResponseDto> getMyNotifications(Long facultyId) {

        Faculty faculty = facultyJpaRepo.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        List<Notification> notifications = notificationJpaRepo.findByFaculty_Id(facultyId);

        List<NotificationResponseDto> responseList = new ArrayList<>();
        boolean isEditable = subjectTeacherJpaRepo
                .existsByFaculty_Id(facultyId);
        for (Notification n : notifications) {
            responseList.add(new NotificationResponseDto(
                    n.getId(),
                    n.getMessage(),
                    n.isReadStatus(),
                    n.getCreatedAt(),
                    isEditable
            ));
        }

        return responseList;
    }

    // Mark notification as read
    public String markAsRead(Long facultyId, Long notificationId) {

        Notification notification = notificationJpaRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // Faculty should only read THEIR notification
        if (!notification.getFaculty().getId().equals(facultyId)) {
            throw new RuntimeException("You cannot read someone else's notification");
        }

        notification.setReadStatus(true);
        notificationJpaRepo.save(notification);

        return "Notification marked as read";
    }

    // Utility method for HOD to send notifications
    public void sendNotification(Faculty faculty, String message) {

        Notification notification = new Notification();
        notification.setFaculty(faculty);
        notification.setMessage(message);

        notificationJpaRepo.save(notification);
    }
}
