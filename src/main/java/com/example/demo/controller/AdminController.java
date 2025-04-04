package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.DataResponse;
import com.example.demo.dto.AccountDTO;
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
}
