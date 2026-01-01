package com.example.SmartCampusConnect.dtos.subjectTeacherDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectTeacherResponseDto {

    private Long id;

    private Long facultyId;

    private String facultyName;

    private String subjectName;

    private String branch;

    private String section;

    private Integer year;
}
