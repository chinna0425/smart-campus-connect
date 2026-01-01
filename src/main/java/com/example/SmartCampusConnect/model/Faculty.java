package com.example.SmartCampusConnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="faculty")
public class Faculty {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false)
    private String branch;

    public Faculty(String branch,String name, User user,String employeeId) {
        this.branch=branch;
        this.name=name;
        this.user=user;
        this.employeeId=employeeId;
    }
}
