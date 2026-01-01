package com.example.SmartCampusConnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "student")
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    private Long studentId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false,unique = true)
    private String rollNumber;
    @Column(nullable = false)
    private String branch;
    @Column(nullable = false)
    private String section;
    @Column(nullable = false)
    private Integer year;

    public Student(String name, String branch,Integer year,String rollNumber, User user, String section) {
        this.name=name;
        this.branch=branch;
        this.year=year;
        this.rollNumber=rollNumber;
        this.user=user;
        this.section=section;
    }
}
