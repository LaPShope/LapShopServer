package com.example.demo.controller;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.LaptopModelDTO;
import com.example.demo.dto.response.LaptopModelResponse;
import com.example.demo.service.LaptopModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptop-models")
public class LaptopModelController {

    private final LaptopModelService laptopModelService;

    public LaptopModelController(LaptopModelService laptopModelService) {
        this.laptopModelService = laptopModelService;
    }

    // 1. Lấy danh sách tất cả LaptopModels
    @GetMapping
    public ResponseEntity<?> getAllLaptopModels() {

        return ResponseEntity.ok(DataResponse.<List<LaptopModelResponse>>builder()
                .success(true)
                .message("LaptopModel retrieved successfully")
                .data(laptopModelService.getAllLaptopModels())
                .build());
    }

    // 2. Lấy LaptopModel theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getLaptopModelById(@PathVariable UUID id) {

        return ResponseEntity.ok(DataResponse.<LaptopModelResponse>builder()
                .success(true)
                .message("LaptopModel retrieved successfully")
                .data(laptopModelService.getLaptopModelById(id))
                .build());

    }

    // 3. Tạo mới một LaptopModel
    @PostMapping
    public ResponseEntity<?> createLaptopModel(@RequestBody LaptopModelDTO laptopModelDTO) {


        return ResponseEntity.ok(DataResponse.<LaptopModelResponse>builder()
                .success(true)
                .message("LaptopModel created successfully")
                .data(laptopModelService.createLaptopModel(laptopModelDTO))
                .build());

    }

    // 4. Cập nhật một LaptopModel
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLaptopModel(@PathVariable UUID id, @RequestBody LaptopModelDTO laptopModelDTO) {


        return ResponseEntity.ok(DataResponse.<LaptopModelResponse>builder()
                .success(true)
                .message("LaptopModel updated successfully")
                .data(laptopModelService.updateLaptopModel(id, laptopModelDTO))
                .build());

    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateLaptopModel(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<LaptopModelResponse>builder()
                .success(true)
                .message("LaptopModel updated successfully")
                .data(laptopModelService.partialUpdateLaptopModel(id, fieldsToUpdate))
                .build());
    }

    // 5. Xóa một LaptopModel
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLaptopModel(@PathVariable UUID id) {

            laptopModelService.deleteLaptopModel(id);
        return ResponseEntity.ok(DataResponse.<LaptopModelDTO>builder()
                .success(true)
                .message("LaptopModel deleted successfully")
                .build());

    }
}