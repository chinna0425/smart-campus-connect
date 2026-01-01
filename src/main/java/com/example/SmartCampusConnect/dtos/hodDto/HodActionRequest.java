package com.example.SmartCampusConnect.dtos.hodDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HodActionRequest {

    @Schema(description = "Request ID on which action is performed", example = "15")
    @NotNull(message = "Request ID is required")
    private Long requestId;

    @Schema(description = "Approval status (true = approve, false = reject)", example = "true")
    @NotNull(message = "Approval decision is required")
    private Boolean approved;

    @Schema(description = "Remarks (required only when rejected)", example = "Insufficient documents submitted")
    private String remarks;
}
