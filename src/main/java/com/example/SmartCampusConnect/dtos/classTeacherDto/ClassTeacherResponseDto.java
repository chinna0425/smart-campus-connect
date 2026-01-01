package com.example.SmartCampusConnect.dtos.classTeacherDto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassTeacherResponseDto {
    private Long id;
    private Long facultyId;
    private String facultyName;
    private String branch;
    private String section;
    private Integer year;
}
