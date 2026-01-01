package com.example.SmartCampusConnect.dtos.facultyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyActionRequest {
    @Schema(description = "Request ID on which faculty is taking action", example = "10")
    @NotNull(message = "Request ID is required")
    private Long requestId;

    @Schema(description = "Is request approved? (true = approve, false = reject)", example = "true")
    @NotNull(message = "Approval decision is required")
    private Boolean approved;

    @Schema(description = "Remarks (required only when rejected)", example = "Insufficient proof provided")
    private String remarks;
}
