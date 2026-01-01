package com.example.SmartCampusConnect.dtos.AdminDto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.examples.Example;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminCreateDto {

    @Schema(description = "Name is required", example = "Kiran Kumar")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Email is required", example = "kiran@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    @Schema(description = "Password is required", example = "*****")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}

