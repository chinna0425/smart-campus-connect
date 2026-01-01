package com.example.SmartCampusConnect.dtos.NotificationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private Long id;
    private String message;
    private boolean readStatus;
    private LocalDateTime createdAt;
    private boolean isEditable;
}