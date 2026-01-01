package com.example.SmartCampusConnect.dtos.subjectTeacherDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectTeacherUpdateRequest {
    @Schema(
            description = "Updated Faculty ID for the subject teacher (optional)",
            example = "15"
    )
    private Long facultyId;

    @Schema(
            description = "Updated subject name (optional)",
            example = "Mathematics"
    )
    private String subjectName;

    @Schema(
            description = "Updated branch (optional)",
            example = "CSE"
    )
    private String branch;

    @Schema(
            description = "Updated section (optional)",
            example = "A"
    )
    private String section;

    @Schema(
            description = "Updated year of study (1 to 4) (optional)",
            example = "2"
    )
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year cannot be more than 4")
    private Integer year;

    // optional: helper method to check if update DTO is empty
    public boolean isEmpty() {
        return isBlank(subjectName) &&
                isBlank(branch) &&
                isBlank(section) &&
                year == null &&
                facultyId == null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
