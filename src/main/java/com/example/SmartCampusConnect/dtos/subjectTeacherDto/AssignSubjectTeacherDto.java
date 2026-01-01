package com.example.SmartCampusConnect.dtos.subjectTeacherDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignSubjectTeacherDto {

    @Schema(
            description = "Faculty ID of the teacher to assign for the subject",
            example = "15"
    )
    @NotNull(message = "Faculty ID is required")
    private Long facultyId;

    @Schema(
            description = "Name of the subject to assign",
            example = "Mathematics"
    )
    @NotBlank(message = "Subject name is required")
    private String subjectName;

    @Schema(
            description = "Branch for which the subject teacher is assigned",
            example = "CSE"
    )
    @NotBlank(message = "Branch is required")
    private String branch;

    @Schema(
            description = "Section for which the subject teacher is assigned",
            example = "A"
    )
    @NotBlank(message = "Section is required")
    private String section;

    @Schema(
            description = "Year of study (1 to 4)",
            example = "2"
    )
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year cannot be more than 4")
    private int year;
}

