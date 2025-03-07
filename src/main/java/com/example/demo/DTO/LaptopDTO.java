package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import com.example.demo.Common.Enums;


import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaptopDTO {
    @JsonProperty("mac_id")
    private UUID macId;

    private Date MFG;
    @JsonProperty("laptop_model_id")
    private UUID laptopModelId;

    private Enums.laptopStatus status;
}