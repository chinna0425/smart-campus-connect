package com.example.SmartCampusConnect.dtos.request;

import com.example.SmartCampusConnect.enums.RequestStatus;
import com.example.SmartCampusConnect.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponse {
    private Long id;
    private RequestType requestType;
    private RequestStatus status;
    private String description;

    private String studentName;
    private String facultyName;

    private LocalDate fromDate;
    private LocalDate toDate;

    private String facultyRemarks;   // filled after faculty action
    private String hodRemarks;       // filled after HOD action

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
