package com.example.SmartCampusConnect.dtos.facultyDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyResponse {
    private Long id;
    private String name;
    private String employeeId;
    private String branch;
}
