package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleDTO {
    private UUID id;
    @JsonProperty("event_description")
    private String eventDescription;
    @JsonProperty("start_at")
    private Date startAt;
    @JsonProperty("end_at")
    private Date endAt;
    private Float discount;
    @JsonProperty("laptop_model_ids")
    private List<UUID> laptopModelIds;
}