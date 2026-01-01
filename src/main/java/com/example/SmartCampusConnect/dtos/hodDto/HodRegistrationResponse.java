package com.example.SmartCampusConnect.dtos.hodDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HodRegistrationResponse {
    private Long id;
    private String name;
    private String branch;
    private String hodId;
    private String message;
}
