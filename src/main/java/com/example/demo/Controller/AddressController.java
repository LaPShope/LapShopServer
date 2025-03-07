package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.AccountDTO;
import com.example.demo.DTO.AddressDTO;
import com.example.demo.DTO.Response.AddressResponse;
import com.example.demo.Service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // Lấy tất cả địa chỉ của một khách hàng
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<DataResponse<List<AddressResponse>>> getAllAddress(@PathVariable UUID customerId) {
        return ResponseEntity.ok(DataResponse.<List<AddressResponse>>builder()
                .success(true)
                .message("Addresses retrieved successfully")
                .data(addressService.getAllAddress(customerId))
                .build());
    }

    // Lấy địa chỉ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<AddressResponse>> getAddressById(@PathVariable UUID id) {
        return ResponseEntity.ok(DataResponse.<AddressResponse>builder()
                .success(true)
                .message("Address retrieved successfully")
                .data(addressService.getAddressById(id))
                .build());
    }
    // Tạo mới địa chỉ
    @PostMapping
    public ResponseEntity<DataResponse<?>> createAddress(@RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(DataResponse.<AddressResponse>builder()
                        .success(true)
                        .message("Address created successfully!")
                        .data(addressService.createAddress(addressDTO))
                        .build());
    }

    // Cập nhật thông tin địa chỉ
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable UUID id, @RequestBody AddressDTO updatedAddress) {
            return ResponseEntity.ok(DataResponse.<AddressResponse>builder()
                    .success(true)
                    .message("Address updated successfully!")
                    .data(addressService.updateAddress(id,updatedAddress))
                    .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResponse<AddressResponse>> partialUpdateAddress(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fieldsToUpdate) {

        return ResponseEntity.ok(DataResponse.<AddressResponse>builder()
                .success(true)
                .message("Address updated successfully!")
                .data(addressService.partialUpdateAddress(id, fieldsToUpdate))
                .build());
    }


    // Xóa địa chỉ
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable UUID id) {
            addressService.deleteAddress(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Address deleted successfully!")
                .build());
    }
}