package com.example.SmartCampusConnect.dtos.facultyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyRegistrationRequest {

    @Schema(
            description = "Faculty email address",
            example = "john.doe@college.edu"
    )
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "Password for faculty login",
            example = "P@ssw0rd123"
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Schema(
            description = "Full name of the faculty",
            example = "John Doe"
    )
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(
            description = "Branch / Department of the faculty",
            example = "CSE"
    )
    @NotBlank(message = "Branch is required")
    private String branch;

}
