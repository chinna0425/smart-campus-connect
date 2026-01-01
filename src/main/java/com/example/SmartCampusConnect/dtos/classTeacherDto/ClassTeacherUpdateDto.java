package com.example.SmartCampusConnect.dtos.classTeacherDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassTeacherUpdateDto {
    @Schema(description = "Updated faculty ID to assign as class teacher (optional)", example = "12")
    private Long facultyId;

    @Schema(description = "Updated branch of the class teacher", example = "CSE")
    private String branch;

    @Schema(description = "Updated section of the class teacher", example = "A")
    private String section;

    @Schema(description = "Updated year of study (1 to 4)", example = "2")
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year cannot exceed 4")
    private Integer year;

    public boolean isEmpty(){
        return facultyId==null && branch==null && section==null && year==null;
    }
}
