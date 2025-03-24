package com.example.demo.dto.response;

import com.example.demo.common.Enums;
import com.example.demo.dto.LaptopModelDTO;
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
