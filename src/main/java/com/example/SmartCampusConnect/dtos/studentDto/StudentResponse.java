package com.example.SmartCampusConnect.dtos.studentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private Long id;
    private String name;
    private String rollNumber;
    private String branch;
    private String section;
    private Integer year;
}
