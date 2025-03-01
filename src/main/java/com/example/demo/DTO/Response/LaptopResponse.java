package com.example.demo.DTO.Response;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.LaptopModelDTO;
import com.example.demo.Models.LaptopModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaptopResponse {

        @JsonProperty("mac_id")
        private UUID macId;

        private Date MFG;
        @JsonProperty("laptop_model")
        private LaptopModelDTO laptopModel;

        private Enums.laptopStatus status;

}
