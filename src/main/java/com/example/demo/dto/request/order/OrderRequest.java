package com.example.demo.dto.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private String address;
    private String phone;
    private String note;
}
