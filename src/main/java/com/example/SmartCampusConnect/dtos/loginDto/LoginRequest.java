package com.example.SmartCampusConnect.dtos.loginDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Schema(description = "Enter the email",example = "******@acoe.edu.in")
    @NotBlank(message = "email required for login")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Enter the password",example = "*****")
    @NotBlank(message = "password required for login")
    private String password;

    @Schema(description = "Enter the role",example = "STUDENT or FACULTY or HOD")
    @NotBlank(message = "Role required for login")
    private String role;
}
