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
@Table(name = "hod")
public class Hod {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String hodId;

    @Column(nullable = false, unique = true)
    private String branch;

    public Hod(String branch,String name, User user) {
        this.branch=branch;
        this.name=name;
        this.user=user;
    }
}
