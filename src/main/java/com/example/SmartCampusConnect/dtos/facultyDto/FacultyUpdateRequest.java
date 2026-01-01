package com.example.SmartCampusConnect.dtos.facultyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyUpdateRequest {
    @Schema(
            description = "Updated name of the faculty (optional)",
            example = "Dr. Ramesh Kumar"
    )
    private String name;

    @Schema(
            description = "Updated branch/department of the faculty (optional)",
            example = "CSE"
    )
    private String branch;

    public boolean isEmpty(){
        return name==null && branch==null;
    }
}
