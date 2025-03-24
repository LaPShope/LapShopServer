package com.example.demo.DTO.Response;

import com.example.demo.DTO.LaptopModelDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class SaleResponse {
    private UUID id;
    @JsonProperty("event_description")
    private String eventDescription;
    @JsonProperty("start_at")
    private Date startAt;
    @JsonProperty("end_at")
    private Date endAt;
    private Float discount;
    @JsonProperty("laptop_model_list")
    private List<LaptopModelDTO> laptopModelList;
}
