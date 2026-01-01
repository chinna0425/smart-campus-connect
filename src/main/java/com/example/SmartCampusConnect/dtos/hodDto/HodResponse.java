package com.example.SmartCampusConnect.dtos.hodDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HodResponse {
    private Long id;

    private String name;

    private String branch;

    private String hodId;

}
