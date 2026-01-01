package com.example.SmartCampusConnect.dtos.hodDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HodRegistrationRequest {
    @Schema(
            description = "HOD email address for registration",
            example = "hod.cse@college.edu"
    )
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "Password for HOD account",
            example = "StrongP@ss123"
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Schema(
            description = "Full name of the HOD",
            example = "Dr. Ramesh Kumar"
    )
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(
            description = "Branch handled by the HOD",
            example = "CSE"
    )
    @NotBlank(message = "Branch is required")
    private String branch;
}
