package com.example.demo.repository;

import com.example.demo.dto.response.LaptopResponse;

import java.util.List;
import java.util.Map;

public interface LaptopQueryRepository {
    List<LaptopResponse> searchLaptopsByLaptopModelAndLaptop(Map<String, Object> filters);
}
