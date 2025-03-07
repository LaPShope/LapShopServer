package com.example.demo.Repository;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.Response.LaptopResponse;
import com.example.demo.Models.Laptop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface LaptopQueryRepository {
    List<LaptopResponse> searchLaptopsByLaptopModelAndLaptop(Map<String, Object> filters);
}
