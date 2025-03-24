package com.example.demo.Service;

import com.example.demo.DTO.LaptopModelDTO;
import com.example.demo.DTO.Response.LaptopModelResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LaptopModelService {
    LaptopModelResponse partialUpdateLaptopModel(UUID id, Map<String,Object> fieldsToUpdate);
    List<LaptopModelResponse> getAllLaptopModels();                  // Lấy tất cả LaptopModel
    LaptopModelResponse getLaptopModelById(UUID id);                 // Lấy LaptopModel theo ID
    LaptopModelResponse createLaptopModel(LaptopModelDTO laptopModelDTO);      // Tạo LaptopModel mới
    LaptopModelResponse updateLaptopModel(UUID id, LaptopModelDTO laptopModelDTO); // Cập nhật LaptopModel theo ID
    void deleteLaptopModel(UUID id);                            // Xóa LaptopModel theo ID
}