package com.example.SmartCampusConnect.dtos.facultyDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyRegistrationResponse {
    private Long id;
    private String name;
    private String employeeId;
    private String branch;
    private String message;
}
