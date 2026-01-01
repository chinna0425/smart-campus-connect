package com.example.SmartCampusConnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "class_teachers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"faculty_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(nullable = false)
    private String branch;   // CSE, ECE, IT

    @Column(nullable = false)
    private String section;  // A, B, C

    @Column(nullable = false)
    private Integer year;        // 1, 2, 3, 4
}
