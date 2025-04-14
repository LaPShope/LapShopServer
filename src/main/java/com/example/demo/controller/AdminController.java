package com.example.demo.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.dto.response.AdminResponse;
import com.example.demo.service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<DataResponse<AdminResponse>> register(@RequestBody AccountDTO accountDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.<AdminResponse>builder()
                        .success(true)
                        .message("Account created successfully!")
                        .data(adminService.createAdmin(accountDTO))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<AccountResponse>> updateAccount(
            @PathVariable UUID id,
            @RequestBody AccountDTO accountDTO) {

        AccountResponse updated = adminService.updateAccount(id, accountDTO);

        return ResponseEntity.ok(
                DataResponse.<AccountResponse>builder()
                        .success(true)
                        .message("Account updated successfully")
                        .data(updated)
                        .build()
        );
    }

    // ✅ Cập nhật một phần tài khoản (partial)
    @PatchMapping("/{id}")
    public ResponseEntity<DataResponse<AccountResponse>> partialUpdateAccount(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fieldsToUpdate) {

        AccountResponse updated = adminService.partialUpdateAccount(id, fieldsToUpdate);

        return ResponseEntity.ok(
                DataResponse.<AccountResponse>builder()
                        .success(true)
                        .message("Account partially updated successfully")
                        .data(updated)
                        .build()
        );
    }
}
