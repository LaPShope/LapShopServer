package com.example.demo.controller;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.ImageDTO;
import com.example.demo.service.ImageService;
import com.example.demo.service.impl.DriveUploader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images") // Định nghĩa URL cơ bản cho các API liên quan đến Image
public class ImageController {

    private final ImageService imageService;
    private final DriveUploader uploader;

    public ImageController(ImageService imageService, DriveUploader uploader) {
        this.imageService = imageService;
        this.uploader = uploader;
    }

    // 1. Lấy danh sách tất cả Images
    @GetMapping
    public ResponseEntity<DataResponse<List<ImageDTO>>> getAllImages() {
        return ResponseEntity.ok(DataResponse.<List<ImageDTO>>builder()
                .success(true)
                .message("Image retrieved successfully")
                .data(imageService.getAllImages())
                .build());
    }

    // 2. Lấy Image theo ID
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<ImageDTO>> getImageById(@PathVariable UUID id) {

        return ResponseEntity.ok(DataResponse.<ImageDTO>builder()
                .success(true)
                .message("Image retrieved successfully")
                .data(imageService.getImageById(id))
                .build());
    }

    // 3. Tạo mới một Image
    @PostMapping
    public ResponseEntity<?> createImage(@RequestBody ImageDTO imageDTO) {
        return ResponseEntity.ok(DataResponse.<ImageDTO>builder()
                .success(true)
                .message("Image created successfully")
                .data(imageService.createImage(imageDTO))
                .build());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String fileUrl = uploader.uploadImageToDrive(image);
            System.out.println("File URL: " + fileUrl);
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setImageUrl(fileUrl);
            return ResponseEntity.ok(DataResponse.<ImageDTO>builder()
                    .success(true)
                    .message("Image uploaded successfully")
                    .data(imageService.createImage(imageDTO))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(DataResponse.builder()
                    .success(false)
                    .message("Failed to upload image: " + e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImage(@PathVariable UUID id, @RequestBody ImageDTO imageDTO) {

        return ResponseEntity.ok(DataResponse.<ImageDTO>builder()
                .success(true)
                .message("Image updated successfully")
                .data(imageService.updateImage(id, imageDTO))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateImage(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<ImageDTO>builder()
                .success(true)
                .message("Image updated successfully")
                .data(imageService.partialUpdateImage(id, fieldsToUpdate))
                .build());
    }


    // 5. Xóa Image theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Image deleted successfully")
                .build());
    }
}