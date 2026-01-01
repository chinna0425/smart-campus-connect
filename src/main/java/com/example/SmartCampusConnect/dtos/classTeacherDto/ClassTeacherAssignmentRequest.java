package com.example.SmartCampusConnect.dtos.classTeacherDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassTeacherAssignmentRequest {
    @Schema(
            description = "Faculty ID to assign as class teacher",
            example = "12"
    )
    @NotNull(message = "FacultyId is required")
    private Long facultyId;

    @Schema(
            description = "Branch of the class teacher",
            example = "CSE"
    )
    @NotBlank(message = "Branch is required")
    private String branch;

    @Schema(
            description = "Section of the class teacher",
            example = "A"
    )
    @NotBlank(message = "Section is required")
    private String section;

    @Schema(
            description = "Year of study (1 to 4)",
            example = "3"
    )
    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year cannot exceed 4")
    private Integer year;

}
