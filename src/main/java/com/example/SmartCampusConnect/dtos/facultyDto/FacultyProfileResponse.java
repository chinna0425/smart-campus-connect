package com.example.SmartCampusConnect.dtos.facultyDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyProfileResponse {
    private Long id;
    private String name;
    private String employeeId;
    private String branch;

    private boolean isClassTeacher;   // true/false
    private String ctBranch;
    private String ctSection;
    private Integer ctYear;
}
