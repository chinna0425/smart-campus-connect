package com.example.SmartCampusConnect.model;

import com.example.SmartCampusConnect.enums.RequestStatus;
import com.example.SmartCampusConnect.enums.RequestType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student who created the request
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Assigned class teacher (faculty)
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(nullable = false)
    private String description;

    // Leave only fields
    private LocalDate fromDate;
    private LocalDate toDate;

    // Remarks
    private String facultyRemarks;
    private String hodRemarks;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
