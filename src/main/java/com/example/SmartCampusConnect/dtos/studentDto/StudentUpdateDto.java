package com.example.SmartCampusConnect.dtos.studentDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentUpdateDto {

    @Schema(
            description = "Updated full name of the student",
            example = "Kiran Kumar"
    )
    private String name;

    @Schema(
            description = "Updated branch/department of the student",
            example = "CSE"
    )
    private String branch;

    @Schema(
            description = "Updated class section",
            example = "A"
    )
    private String section;

    @Schema(
            description = "Updated year of study (1 to 4)",
            example = "2"
    )
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year cannot exceed 4")
    private Integer year;


    public boolean isEmpty(){
        return name==null && branch==null && section==null && year==null;
    }
}
