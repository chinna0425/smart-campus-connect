package com.example.SmartCampusConnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Extra fields for Admin profile
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String adminId; // Example: AD001

}

