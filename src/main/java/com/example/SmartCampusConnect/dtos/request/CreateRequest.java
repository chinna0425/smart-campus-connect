package com.example.SmartCampusConnect.dtos.request;

import com.example.SmartCampusConnect.enums.RequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequest {
    @Schema(
            description = "Type of request",
            example = "LEAVE"
    )
    @NotNull(message = "Request Type is required")
    private RequestType requestType;

    @Schema(
            description = "Description of the request",
            example = "I need leave for a family function"
    )
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(
            description = "From date (required only for LEAVE requests)",
            example = "2025-01-10"
    )
    private LocalDate fromDate;

    @Schema(
            description = "To date (required only for LEAVE requests)",
            example = "2025-01-12"
    )
    private LocalDate toDate;
}
