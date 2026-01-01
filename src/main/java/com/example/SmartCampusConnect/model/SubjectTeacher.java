package com.example.SmartCampusConnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subject_teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(nullable = false)
    private String subjectName;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private Integer year;
}
