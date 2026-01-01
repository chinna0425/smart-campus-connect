package com.example.SmartCampusConnect.dtos.hodDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HodUpdateReq {

    @Schema(
            description = "Full name of the HOD",
            example = "Dr. Ramesh Kumar"
    )
    private String name;

    @Schema(
            description = "Branch handled by the HOD",
            example = "CSE"
    )
    private String branch;

    public boolean isBlank(){
        return name==null && branch==null;
    }
}
