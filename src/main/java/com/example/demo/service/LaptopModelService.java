package com.example.demo.service;

import com.example.demo.dto.LaptopModelDTO;
import com.example.demo.dto.response.LaptopModelResponse;
import com.example.demo.dto.response.PagingResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LaptopModelService {
    List<LaptopModelResponse> getAllLaptopModels();                  // Lấy tất cả LaptopModel

    LaptopModelResponse partialUpdateLaptopModel(UUID id, Map<String, Object> fieldsToUpdate);

    LaptopModelResponse getLaptopModelById(UUID id);                 // Lấy LaptopModel theo ID

    LaptopModelResponse createLaptopModel(LaptopModelDTO laptopModelDTO);      // Tạo LaptopModel mới

    LaptopModelResponse updateLaptopModel(UUID id, LaptopModelDTO laptopModelDTO); // Cập nhật LaptopModel theo ID

    void deleteLaptopModel(UUID id);                            // Xóa LaptopModel theo ID

    PagingResponse<?> getLaptopModelsWithPaginationAndSortByPriceASC(double price, int offset, int pageSize);

    PagingResponse<?> getLaptopModelsWithPaginationAndSortByPriceDES(double price, int offset, int pageSize);

    PagingResponse<?> getLaptopModelsWithPagination(int offset, int pageSize);

    LaptopModelResponse addImageToLaptopModel(UUID laptopModelId, UUID imageId);

    LaptopModelResponse addImageToLaptopModelV2(UUID laptopModelId, List<UUID> imageIds);
}