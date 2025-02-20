package com.example.demo.DTO;

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
    private String eventDescription;
    private Date startAt;
    private Date endAt;
    private Float discount;
    private List<UUID> laptopModelIds;
}