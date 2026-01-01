package com.example.SmartCampusConnect.dtos.studentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationResponse {
    private Long id;
    private String name;
    private String rollNumber;
    private String branch;
    private String section;
    private Integer year;
    private String message;
}
