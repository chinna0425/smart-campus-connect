package com.example.SmartCampusConnect.dtos.studentDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationRequest {
    @Schema(
            description = "Student email address for registration",
            example = "student123@college.edu"
    )
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "Password for student login",
            example = "StrongP@ss123"
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Schema(
            description = "Full name of the student",
            example = "Kiran Kumar"
    )
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(
            description = "Unique roll number assigned to the student",
            example = "22CSE045"
    )
    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    @Schema(
            description = "Branch/Department of the student",
            example = "CSE"
    )
    @NotBlank(message = "Branch is required")
    private String branch;

    @Schema(
            description = "Class section",
            example = "A"
    )
    @NotBlank(message = "Section is required")
    private String section;

    @Schema(
            description = "Year of study (1 to 4)",
            example = "2"
    )
    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year cannot exceed 4")
    private Integer year;

}
